resource "aws_security_group" "instance" {
  name_prefix            = "${var.project_name}-"
  description            = "No inbound access; outbound HTTPS only for SSM and image/package downloads."
  vpc_id                 = aws_vpc.demo.id
  revoke_rules_on_delete = true

  depends_on = [terraform_data.stage_a_guard]

  tags = merge(var.tags, {
    Name        = "${var.project_name}-instance-sg"
    Project     = "iot-industrial"
    Environment = "demo"
  })
}

resource "aws_vpc_security_group_egress_rule" "https" {
  security_group_id = aws_security_group.instance.id
  description       = "HTTPS egress for Systems Manager and package/container image downloads."
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443

  tags = {
    Project     = "iot-industrial"
    Environment = "demo"
  }

  depends_on = [terraform_data.stage_a_guard]
}
