output "vpc_id" {
  description = "VPC identifier to pass explicitly to Stage B and the reviewed IAM policy."
  value       = aws_vpc.demo.id
}

output "subnet_id" {
  description = "Public subnet identifier to pass explicitly to Stage B and its IAM policy."
  value       = aws_subnet.public.id
}

output "security_group_id" {
  description = "Security group identifier to pass explicitly to Stage B and its IAM policy."
  value       = aws_security_group.instance.id
}
