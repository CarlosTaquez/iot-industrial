variable "aws_region" {
  description = "AWS region for Stage A."
  type        = string
  default     = "us-east-2"
}

variable "availability_zone" {
  description = "Availability Zone for the Stage A public subnet."
  type        = string
  default     = "us-east-2a"

  validation {
    condition     = startswith(var.availability_zone, var.aws_region)
    error_message = "availability_zone must belong to aws_region."
  }
}

variable "vpc_cidr" {
  description = "IPv4 CIDR block for the dedicated demonstration VPC."
  type        = string
  default     = "10.42.0.0/16"

  validation {
    condition     = can(cidrhost(var.vpc_cidr, 0))
    error_message = "vpc_cidr must be a valid IPv4 CIDR block."
  }
}

variable "public_subnet_cidr" {
  description = "IPv4 CIDR block for the public subnet."
  type        = string
  default     = "10.42.1.0/24"

  validation {
    condition     = can(cidrhost(var.public_subnet_cidr, 0))
    error_message = "public_subnet_cidr must be a valid IPv4 CIDR block."
  }
}

variable "stage_a_approved" {
  description = "Must remain false until Stage A has a reviewed IAM policy, non-root identity, and cost/security approval."
  type        = bool
  default     = false
}

variable "project_name" {
  description = "Prefix used for Stage A network resource names."
  type        = string
  default     = "iot-industrial-demo"

  validation {
    condition     = can(regex("^[A-Za-z0-9-]{3,32}$", var.project_name))
    error_message = "project_name must be 3 to 32 letters, digits, or hyphens."
  }
}

variable "tags" {
  description = "Additional tags applied to supported Stage A resources."
  type        = map(string)
  default     = {}
}
