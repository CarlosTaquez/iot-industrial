resource "aws_instance" "demo" {
  ami                         = var.ami_id
  instance_type               = var.instance_type
  subnet_id                   = var.subnet_id
  vpc_security_group_ids      = [var.security_group_id]
  iam_instance_profile        = var.instance_profile_name
  associate_public_ip_address = true
  ebs_optimized               = true

  metadata_options {
    http_endpoint               = "enabled"
    http_tokens                 = "required"
    http_put_response_hop_limit = 1
    instance_metadata_tags      = "disabled"
  }

  root_block_device {
    volume_type           = "gp3"
    volume_size           = var.root_volume_size_gib
    encrypted             = true
    delete_on_termination = true
  }

  volume_tags = merge(var.tags, {
    Name        = "${var.project_name}-root-volume"
    Project     = "iot-industrial"
    Environment = "demo"
  })

  depends_on = [
    terraform_data.stage_b_guard,
  ]

  tags = merge(var.tags, {
    Name        = "${var.project_name}-ec2"
    Project     = "iot-industrial"
    Environment = "demo"
  })
}
