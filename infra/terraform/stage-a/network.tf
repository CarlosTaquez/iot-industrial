resource "aws_vpc" "demo" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  depends_on = [terraform_data.stage_a_guard]

  tags = merge(var.tags, {
    Name        = "${var.project_name}-vpc"
    Project     = "iot-industrial"
    Environment = "demo"
  })
}

resource "aws_internet_gateway" "demo" {
  vpc_id = aws_vpc.demo.id

  depends_on = [terraform_data.stage_a_guard]

  tags = merge(var.tags, {
    Name        = "${var.project_name}-igw"
    Project     = "iot-industrial"
    Environment = "demo"
  })
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.demo.id
  cidr_block              = var.public_subnet_cidr
  availability_zone       = var.availability_zone
  map_public_ip_on_launch = true

  depends_on = [terraform_data.stage_a_guard]

  tags = merge(var.tags, {
    Name        = "${var.project_name}-public-subnet"
    Project     = "iot-industrial"
    Environment = "demo"
  })
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.demo.id

  depends_on = [terraform_data.stage_a_guard]

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.demo.id
  }

  tags = merge(var.tags, {
    Name        = "${var.project_name}-public-rt"
    Project     = "iot-industrial"
    Environment = "demo"
  })
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id

  depends_on = [terraform_data.stage_a_guard]
}
