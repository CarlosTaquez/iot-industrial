output "instance_id" {
  description = "EC2 instance identifier for Session Manager."
  value       = aws_instance.demo.id
}

output "aws_region" {
  description = "AWS region containing the demonstration instance."
  value       = var.aws_region
}

output "instance_private_ip" {
  description = "Private IPv4 address of the instance."
  value       = aws_instance.demo.private_ip
}

output "instance_public_ip" {
  description = "Automatically assigned public IPv4 address; AWS normally releases it when the instance is stopped."
  value       = aws_instance.demo.public_ip
}

output "primary_network_interface_id" {
  description = "Identifier of the primary interface created implicitly by EC2."
  value       = aws_instance.demo.primary_network_interface_id
}

output "root_volume_id" {
  description = "Identifier of the encrypted root EBS volume, deleted with the instance."
  value       = aws_instance.demo.root_block_device[0].volume_id
}

output "ssm_start_session_command" {
  description = "Command for an operator with the separate restricted Session Manager policy and local plugin."
  value       = "aws ssm start-session --target ${aws_instance.demo.id} --region ${var.aws_region}"
}
