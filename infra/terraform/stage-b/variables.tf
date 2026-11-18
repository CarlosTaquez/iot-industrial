variable "aws_region" {
  description = "AWS region containing the Stage A network and Stage B instance."
  type        = string
  default     = "us-east-2"
}

variable "vpc_id" {
  description = "Stage A VPC ID used to verify that subnet and security group belong to the same project VPC."
  type        = string

  validation {
    condition     = can(regex("^vpc-[0-9a-f]{8,17}$", var.vpc_id))
    error_message = "vpc_id must be copied from Stage A output."
  }
}

variable "subnet_id" {
  description = "Public subnet ID from Stage A output."
  type        = string

  validation {
    condition     = can(regex("^subnet-[0-9a-f]{8,17}$", var.subnet_id))
    error_message = "subnet_id must be copied from Stage A output."
  }
}

variable "security_group_id" {
  description = "Security group ID from Stage A output."
  type        = string

  validation {
    condition     = can(regex("^sg-[0-9a-f]{8,17}$", var.security_group_id))
    error_message = "security_group_id must be copied from Stage A output."
  }
}

variable "instance_profile_name" {
  description = "Name of the externally provisioned SSM instance profile; example: iot-industrial-demo-instance-profile."
  type        = string
}

variable "ami_id" {
  description = "Approved Amazon Linux 2023 x86_64 AMI ID for aws_region."
  type        = string

  validation {
    condition     = can(regex("^ami-([0-9a-f]{8}|[0-9a-f]{17})$", var.ami_id))
    error_message = "ami_id must be a syntactically valid AMI ID."
  }
}

variable "ami_id_approved" {
  description = "Set true only after independently verifying the AMI ID, owner, architecture, OS, state, and region."
  type        = bool
  default     = false
}

variable "instance_type" {
  description = "EC2 instance type. m7i-flex.large is a candidate, not a cost guarantee."
  type        = string
  default     = "m7i-flex.large"

  validation {
    condition     = length(trimspace(var.instance_type)) > 0
    error_message = "instance_type must not be empty."
  }
}

variable "root_volume_size_gib" {
  description = "Encrypted gp3 root EBS volume size in GiB."
  type        = number
  default     = 20

  validation {
    condition     = var.root_volume_size_gib >= 20 && floor(var.root_volume_size_gib) == var.root_volume_size_gib
    error_message = "root_volume_size_gib must be a whole number of at least 20 GiB."
  }
}

variable "stage_b_approved" {
  description = "Must remain false until Stage B IAM permissions, Stage A IDs, security, and costs are reviewed."
  type        = bool
  default     = false
}

variable "project_name" {
  description = "Prefix used for Stage B resource names and tags. Must match Stage A project_name."
  type        = string
  default     = "iot-industrial-demo"

  validation {
    condition     = can(regex("^[A-Za-z0-9-]{3,32}$", var.project_name))
    error_message = "project_name must be 3 to 32 letters, digits, or hyphens."
  }
}

variable "tags" {
  description = "Additional tags applied to supported Stage B resources."
  type        = map(string)
  default     = {}
}
