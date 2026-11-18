provider "aws" {
  region = var.aws_region
}

data "aws_caller_identity" "current" {}
data "aws_partition" "current" {}

resource "terraform_data" "stage_a_guard" {
  input = {
    approved   = var.stage_a_approved
    caller_arn = data.aws_caller_identity.current.arn
  }

  lifecycle {
    precondition {
      condition     = var.stage_a_approved
      error_message = "Stage A is blocked. Set stage_a_approved = true only after reviewing this stage's IAM policy, security, and costs."
    }

    precondition {
      condition     = data.aws_caller_identity.current.arn != "arn:${data.aws_partition.current.partition}:iam::${data.aws_caller_identity.current.account_id}:root"
      error_message = "Stage A is blocked because Terraform is using the AWS root principal. Use an authorized non-root identity."
    }
  }
}
