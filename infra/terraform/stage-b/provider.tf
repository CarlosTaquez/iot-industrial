provider "aws" {
  region = var.aws_region
}

data "aws_caller_identity" "current" {}
data "aws_partition" "current" {}

# These reads verify that the handoff identifiers still refer to Stage A resources.
data "aws_subnet" "stage_a" {
  id = var.subnet_id
}

data "aws_security_group" "stage_a" {
  id = var.security_group_id
}

resource "terraform_data" "stage_b_guard" {
  input = {
    approved                   = var.stage_b_approved
    ami_id_approved            = var.ami_id_approved
    ami_id                     = var.ami_id
    caller_arn                 = data.aws_caller_identity.current.arn
    vpc_id                     = var.vpc_id
    subnet_id                  = var.subnet_id
    subnet_vpc_id              = data.aws_subnet.stage_a.vpc_id
    security_group_id          = var.security_group_id
    security_group_vpc         = data.aws_security_group.stage_a.vpc_id
    subnet_project             = lookup(data.aws_subnet.stage_a.tags, "Project", "")
    subnet_environment         = lookup(data.aws_subnet.stage_a.tags, "Environment", "")
    security_group_project     = lookup(data.aws_security_group.stage_a.tags, "Project", "")
    security_group_environment = lookup(data.aws_security_group.stage_a.tags, "Environment", "")
  }

  lifecycle {
    precondition {
      condition     = var.stage_b_approved
      error_message = "Stage B is blocked. Set stage_b_approved = true only after reviewing the exact Stage A IDs, IAM policy, security, and costs."
    }

    precondition {
      condition     = data.aws_caller_identity.current.arn != "arn:${data.aws_partition.current.partition}:iam::${data.aws_caller_identity.current.account_id}:root"
      error_message = "Stage B is blocked because Terraform is using the AWS root principal. Use an authorized non-root identity."
    }

    precondition {
      condition     = var.ami_id_approved && can(regex("^ami-([0-9a-f]{8}|[0-9a-f]{17})$", var.ami_id))
      error_message = "Stage B is blocked until ami_id is verified for the configured region and ami_id_approved is true."
    }

    precondition {
      condition = (
        data.aws_subnet.stage_a.vpc_id == var.vpc_id &&
        data.aws_security_group.stage_a.vpc_id == var.vpc_id &&
        lookup(data.aws_subnet.stage_a.tags, "Project", "") == "iot-industrial" &&
        lookup(data.aws_subnet.stage_a.tags, "Environment", "") == "demo" &&
        lookup(data.aws_security_group.stage_a.tags, "Project", "") == "iot-industrial" &&
        lookup(data.aws_security_group.stage_a.tags, "Environment", "") == "demo"
      )
      error_message = "Stage B is blocked because the supplied subnet and security group must belong to vpc_id and carry the Stage A project/environment tags."
    }
  }
}
