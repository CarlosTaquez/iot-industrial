# Infraestructura Terraform por etapas

## Estado real y alcance de esta configuración

El despliegue AWS de demostración de IoT Industrial está funcionando en la instancia existente de `us-east-2`, según las validaciones operativas reportadas. Eso no acredita que las raíces Terraform de Stage A y Stage B hayan creado o aplicado esos recursos. Ambas configuraciones pasaron `terraform fmt -check` y `terraform validate`; esos comandos validan formato y configuración, no aplican infraestructura ni prueban que el estado Terraform corresponda a los recursos AWS activos.

No ejecutes un plan contra la instancia existente sin confirmar primero que el estado independiente de Stage B la administra y que los valores de configuración coinciden. No se debe inferir el estado remoto a partir de los archivos `.tf` ni del despliegue Docker.

Esta carpeta contiene dos configuraciones raíz independientes. Terraform debe ejecutarse desde `stage-a` o `stage-b`; el directorio padre no es una raíz aplicable.

## Diseño de las etapas

### Stage A: red

Define una VPC, subnet pública, Internet Gateway, tabla de rutas, ruta y asociación, Security Group sin reglas de entrada y egreso HTTPS TCP 443. Stage A no administra roles, perfiles ni políticas IAM. La configuración no crea EC2.

### Stage B: instancia

Define una instancia EC2 Amazon Linux 2023 x86_64, volumen raíz EBS gp3 cifrado (20 GiB por defecto) e interfaz primaria implícita. Recibe `vpc_id`, `subnet_id`, `security_group_id` y `instance_profile_name` mediante variables. El Security Group no admite conexiones entrantes; la administración se realiza por Session Manager.

La AMI ilustrativa es `ami-0df8a753693e6366d`, verificada anteriormente para `us-east-2`. Hay que confirmar de nuevo el ID, propietario, arquitectura, sistema operativo y estado antes de cualquier despliegue. El archivo de ejemplo mantiene `ami_id_approved = false`.

### Rol y perfil SSM externos

Una identidad administrativa autorizada aprovisiona y elimina por separado el rol `iot-industrial-demo-ssm-role` y el perfil `iot-industrial-demo-instance-profile`. El rol debe confiar en `ec2.amazonaws.com` y tener asociada `AmazonSSMManagedInstanceCore`, con etiquetas `Project=iot-industrial` y `Environment=demo`. Stage B recibe el nombre del perfil; Terraform de estas etapas no crea ni elimina esos recursos IAM.

## Aprobaciones e identidad

- Stage A requiere `stage_a_approved = true`.
- Stage B requiere `stage_b_approved = true` y `ami_id_approved = true`.
- Las aprobaciones permanecen en `false` por defecto en las configuraciones de ejemplo.
- Cada raíz incluye una guarda contra el principal root y condiciona los recursos de esa etapa a su aprobación. Esto no reemplaza una política IAM restrictiva ni una revisión del plan.
- Stage B consulta que la subnet y el Security Group correspondan a la VPC y a las etiquetas de proyecto/entorno. Las fuentes de datos pueden hacer consultas de lectura aunque el despliegue esté bloqueado.

## Paso de identificadores entre etapas

Stage A define outputs `vpc_id`, `subnet_id` y `security_group_id`. Después de una aplicación aprobada, se pueden consultar con `terraform output -raw <nombre>` desde Stage A y proporcionar a Stage B mediante un archivo `terraform.tfvars` local. Añade también el nombre del perfil externo `iot-industrial-demo-instance-profile`. No inventes IDs ni uses `terraform_remote_state` o `terraform apply -target` como flujo permanente.

## Estados y respaldos

Cada raíz utiliza backend local y debe mantener un estado separado. Guarda los estados fuera del repositorio y de OneDrive; pueden contener información sensible. El ejemplo histórico de ruta es `C:\terraform-state\iot-industrial`, pero confirma la ubicación real y el lineage antes de inicializar o usar Terraform. No sobrescribas, muevas ni reemplaces un estado existente para “hacer coincidir” la infraestructura.

El backend local es para un operador y una máquina a la vez. Protege el estado y sus copias con cifrado y permisos locales adecuados; verifica los respaldos antes de cualquier cambio o destrucción. No subas estados, planes, `.tfvars` con datos reales o credenciales a Git.

## Políticas IAM

Los JSON y plantillas de `iam-policies/` son artefactos locales de propuesta/revisión. Su presencia no demuestra que una política esté publicada, adjunta o efectiva en AWS. Las plantillas con marcadores no se deben adjuntar. `terraform-infrastructure.json` continúa incompleta y no debe utilizarse como política completa.

Las políticas Stage A y Stage B requieren revisión de las acciones y condiciones por recurso, especialmente las autorizaciones multi-recurso de EC2, `CreateTags`, ENI y volúmenes durante `RunInstances`, permisos de destrucción y `iam:PassRole`. No presentes como verificadas las políticas sin la validación aplicable de IAM Access Analyzer, simulación y/o una prueba autorizada. No adjuntes permisos administrativos generales. La identidad Terraform debe ser distinta de root.

El acceso humano de Session Manager es independiente del rol SSM de la instancia y de los permisos de despliegue. La política local `iot-industrial-demo-ssm-access-v3.json` está limitada al documento de shell `SSM-SessionManagerRunShell`; por sí sola no autoriza el documento `AWS-StartPortForwardingSession` requerido para túneles del dashboard. Confirma la política efectiva de reenvío de puertos antes de basarte en ella. Ninguna política de usuario reemplaza el instance profile, la conectividad HTTPS o el agente SSM operativo.

## Costos y seguridad

La red de Stage A no añade por sí sola cargos horarios típicos de cómputo; evita añadir NAT Gateway, endpoints, balanceadores u otros servicios sin justificar y revisar su costo. Stage B puede generar cargos por EC2 mientras está encendida, EBS mientras exista, IPv4 pública mientras esté asignada, transferencia y snapshots. La IPv4 pública automática se libera al detener la instancia; EBS puede continuar facturándose. `FreeTierEligible=True` no garantiza costo cero: revisa precios y créditos de Billing justo antes del uso. [Precios de IPv4 pública](https://aws.amazon.com/vpc/pricing/) y [ciclo de vida de direcciones EC2](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-instance-addressing.html).

La subnet/ruta de Internet no implican por sí solas puertos entrantes abiertos. Mantén el Security Group sin ingreso, no publiques Kafka, RabbitMQ ni PostgreSQL, usa Session Manager y protege `.env`, estados y planes. Antes de terminar la EC2, respalda los datos persistentes y confirma el comportamiento de volúmenes y snapshots.

## Validación local

Según la validación reportada, ambas etapas pasaron `terraform fmt -check` y `terraform validate`. Para repetir comprobaciones locales sin plan ni recursos, desde cada directorio de etapa:

```powershell
terraform fmt -check -recursive
terraform init -backend=false
terraform validate
```

`terraform init -backend=false` no inicializa el backend local de estado. No ejecutar `terraform plan`, `apply` ni `destroy` hasta confirmar identidad, estado correspondiente, aprobaciones, permisos, costos y revisión de seguridad. Un plan puede consultar AWS y no equivale a una prueba de aplicación.

## Orden de operación y eliminación

Las etapas son configuraciones independientes; no se debe asumir que Terraform Stage A/B aplicó el despliegue operativo existente. Para una futura operación Terraform, revisa cada plan con su estado correspondiente y autorización explícita.

Cuando se autorice retirar esta infraestructura administrada por Terraform, el orden previsto es:

1. Stage B: terminar la instancia; `delete_on_termination=true` elimina el volumen raíz y la ENI primaria se libera con la instancia.
2. Confirmar que no quedan sesiones, datos o respaldos que conservar.
3. Stage A: retirar dependencias y recursos de red.
4. Por último, una identidad administrativa elimina el instance profile, desvincula `AmazonSSMManagedInstanceCore` y elimina el rol si ninguna instancia lo usa.
5. Revisar Billing y snapshots retenidos.

Conserva los estados y respaldos hasta confirmar la eliminación y cerrar la recuperación. No ejecutes esta secuencia sin plan revisado y autorización.
