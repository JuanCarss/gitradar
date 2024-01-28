provider "aws" {
  access_key = "test"
  secret_key = "test"
  region = "us-east-1"
  s3_use_path_style = true
  skip_credentials_validation = true
  skip_metadata_api_check = true
  skip_requesting_account_id = true
  endpoints {
    apigateway     = "http://host.docker.internal:4566"
    apigatewayv2   = "http://host.docker.internal:4566"
    cloudformation = "http://host.docker.internal:4566"
    cloudwatch     = "http://host.docker.internal:4566"
    dynamodb       = "http://host.docker.internal:4566"
    ec2            = "http://host.docker.internal:4566"
    es             = "http://host.docker.internal:4566"
    elasticache    = "http://host.docker.internal:4566"
    firehose       = "http://host.docker.internal:4566"
    iam            = "http://host.docker.internal:4566"
    kinesis        = "http://host.docker.internal:4566"
    lambda         = "http://host.docker.internal:4566"
    rds            = "http://host.docker.internal:4566"
    redshift       = "http://host.docker.internal:4566"
    route53        = "http://host.docker.internal:4566"
    s3             = "http://host.docker.internal:4566"
    secretsmanager = "http://host.docker.internal:4566"
    ses            = "http://host.docker.internal:4566"
    sns            = "http://host.docker.internal:4566"
    sqs            = "http://host.docker.internal:4566"
    ssm            = "http://host.docker.internal:4566"
    stepfunctions  = "http://host.docker.internal:4566"
    sts            = "http://host.docker.internal:4566"
    eventbridge    = "http://host.docker.internal:4566"
  }
}


# --------------------- BUCKET CREATION ---------------------

resource "aws_s3_bucket" "gitradar-codefiles" {
  bucket = "gitradar-codefiles"
}

resource "aws_s3_bucket" "gitradar-events" {
  bucket = "gitradar-events"
}

resource "aws_s3_bucket" "gitradar-executables" {
  bucket = "gitradar-executables"
}

resource "aws_s3_bucket" "gitradar-models" {
  bucket = "gitradar-models"
}

resource "aws_s3_bucket" "gitradar-metrics" {
  bucket = "gitradar-metrics"
}

# --------------------- UPLOAD CODE TO S3 ---------------------

resource "aws_s3_object" "parser_code" {
  bucket = aws_s3_bucket.gitradar-executables.bucket
  key    = "apps/parser.zip"
  source = "/gitradar/parser.zip"
}

resource "aws_s3_object" "tokenizer_code" {
  bucket = aws_s3_bucket.gitradar-executables.bucket
  key    = "apps/tokenizer.zip"
  source = "/gitradar/tokenizer.zip"
}

resource "aws_s3_object" "event_writer_code" {
  bucket = aws_s3_bucket.gitradar-executables.bucket
  key    = "apps/event_writer.zip"
  source = "/gitradar/infra/utils/event_writer.zip"
}

resource "aws_s3_object" "event_personalizer_code" {
  bucket = aws_s3_bucket.gitradar-executables.bucket
  key    = "apps/event_personalizer.zip"
  source = "/gitradar/infra/utils/event_personalizer.zip"
}

resource "aws_s3_object" "model_trainer_code" {
  bucket = aws_s3_bucket.gitradar-executables.bucket
  key    = "apps/model_trainer.zip"
  source = "/gitradar/model_trainer.zip"
}

resource "aws_s3_object" "name_suggester_code" {
  bucket = aws_s3_bucket.gitradar-executables.bucket
  key    = "apps/name_suggester.zip"
  source = "/gitradar/name_suggester.zip"
}

# --------------------- UPLOAD MODEL TO S3 ---------------------

#resource "aws_s3_object" "model" {
#  for_each = fileset("/gitradar/infra/model", "*")
#  bucket = aws_s3_bucket.gitradar-models.bucket
#  key    = "1706048794.7155528/${each.value}"
#  source = "/gitradar/infra/model/${each.value}"
#}

# --------------------- BUCKET POLICIES ---------------------

resource "aws_s3_bucket_notification" "bucket_notification_codefiles" {
  bucket      = aws_s3_bucket.gitradar-codefiles.id
  eventbridge = true
}

# --------------------- LAMBDA INVOKATION ---------------------

#data "aws_lambda_invocation" "init_model_trainer" {
#  function_name = aws_lambda_function.model_trainer.function_name
#
#    input = <<JSON
#  {
#    "Task": "SetUp"
#  }
#  JSON
#}
#
#data "aws_lambda_invocation" "name_suggester_trainer" {
#  function_name = aws_lambda_function.name_suggester.function_name
#
#    input = <<JSON
#  {
#    "Task": "SetUp"
#  }
#  JSON
#}

# --------------------- LAMBDA FUNCTIONS ---------------------

resource "aws_lambda_function" "parser" {
  s3_bucket = aws_s3_object.parser_code.bucket
  s3_key = aws_s3_object.parser_code.key
  function_name = "parser"
  role          = aws_iam_role.parser.arn
  handler       = "parser.handler"
  runtime       = "python3.8"
  timeout       = 60
  environment {
    variables = {
      CODEFILES_BUCKET_ID = aws_s3_bucket.gitradar-codefiles.id,
      CUSTOM_ENDPOINT_URL = "http://host.docker.internal:4566",
      AWS_REGION = "us-east-1",
      DYNAMODB_TABLE_NAME = aws_dynamodb_table.semantic_tokens.name
    }
  }
}

resource "aws_lambda_function" "tokenizer" {
  s3_bucket = aws_s3_object.tokenizer_code.bucket
  s3_key = aws_s3_object.tokenizer_code.key
  function_name = "tokenizer"
  role          = aws_iam_role.tokenizer.arn
  handler       = "service.handler"
  runtime       = "python3.8"
  timeout       = 60
  environment {
    variables = {
      CODEFILES_BUCKET_ID = aws_s3_bucket.gitradar-codefiles.id,
      CUSTOM_ENDPOINT_URL = "http://host.docker.internal:4566",
      DYNAMODB_TABLE_NAME = aws_dynamodb_table.token_bytes.name
    }
  }
}

resource "aws_lambda_function" "event_writer" {
  s3_bucket = aws_s3_object.event_writer_code.bucket
  s3_key = aws_s3_object.event_writer_code.key
  function_name    = "event_writer"
  role             = aws_iam_role.event_writer.arn
  handler          = "service.handler"
  reserved_concurrent_executions = 1
  runtime = "python3.8"
  timeout       = 60
  environment {
    variables = {
      EVENTS_BUCKET_ID = aws_s3_bucket.gitradar-events.id,
      CUSTOM_ENDPOINT_URL = "http://host.docker.internal:4566"
    }
  }
}

resource "aws_lambda_function" "event_personalizer" {
  s3_bucket = aws_s3_object.event_personalizer_code.bucket
  s3_key = aws_s3_object.event_personalizer_code.key
  function_name    = "event_personalizer"
  role             = aws_iam_role.event_personalizer.arn
  handler          = "service.handler"
  runtime = "python3.8"
  timeout       = 60
  environment {
    variables = {
      CUSTOM_ENDPOINT_URL = "http://host.docker.internal:4566"
    }
  }
}

resource "aws_lambda_function" "code_metrics" {
  filename         = "/gitradar/code_metrics/target/code_metrics-1.0-SNAPSHOT.jar"
  function_name    = "code_metrics"
  role             = aws_iam_role.code_metrics.arn
  handler          = "es.ulpgc.Service::handleRequest"
  runtime = "java21"
  timeout       = 60
  environment {
    variables = {
      METRICS_BUCKET_ID = aws_s3_bucket.gitradar-metrics.id,
      CUSTOM_ENDPOINT_URL = "http://host.docker.internal:4566",
      DYNAMODB_TABLE_NAME = aws_dynamodb_table.semantic_tokens.name
      REGION = "us-east-1"
    }
  }
}

resource "aws_lambda_function" "model_trainer" {
  s3_bucket = aws_s3_object.model_trainer_code.bucket
  s3_key = aws_s3_object.model_trainer_code.key
  function_name    = "model_trainer"
  role             = aws_iam_role.model_trainer.arn
  handler          = "service.handler"
  runtime = "python3.10"
  timeout       = 900 
  reserved_concurrent_executions = 1
  environment {
    variables = {
      CUSTOM_ENDPOINT_URL = "http://host.docker.internal:4566",
      MODELS_BUCKET_ID  = aws_s3_bucket.gitradar-models.id,
      DYNAMODB_TABLE_NAME = aws_dynamodb_table.token_bytes.name
    }
  }
  ephemeral_storage {
    size = 10240 # Min 512 MB and the Max 10240 MB
  }
}

resource "aws_lambda_function" "name_suggester" {
  s3_bucket = aws_s3_object.name_suggester_code.bucket
  s3_key = aws_s3_object.name_suggester_code.key
  function_name    = "name_suggester"
  role             = aws_iam_role.name_suggester.arn
  handler          = "service.handler"
  runtime = "python3.10"
  timeout       = 900 
  reserved_concurrent_executions = 1
  environment {
    variables = {
      CUSTOM_ENDPOINT_URL = "http://host.docker.internal:4566",
      MODELS_BUCKET_ID  = aws_s3_bucket.gitradar-models.id
    }
  }
  ephemeral_storage {
    size = 10240 # Min 512 MB and the Max 10240 MB
  }
}

# --------------------- LAMBDA IAM ROLES ---------------------

resource "aws_iam_role" "parser" {
  name = "parser"
  assume_role_policy = file("./policies/lambda_exec_policy.json")
}

resource "aws_iam_role" "tokenizer" {
  name = "tokenizer"
  assume_role_policy = file("./policies/lambda_exec_policy.json")
}

resource "aws_iam_role" "event_personalizer" {
  name = "iam_event_personalizer_role"
  assume_role_policy = file("./policies/lambda_exec_policy.json")
}

resource "aws_iam_role" "event_writer" {
  name = "iam_event_writer_role"
  assume_role_policy = file("./policies/lambda_exec_policy.json")
}

resource "aws_iam_role" "model_trainer" {
  name = "iam_model_trainer_role"
  assume_role_policy = file("./policies/lambda_exec_policy.json")
}

resource "aws_iam_role" "name_suggester" {
  name = "iam_name_suggester_role"
  assume_role_policy = file("./policies/lambda_exec_policy.json")
}

resource "aws_iam_role" "code_metrics" {
  name = "iam_code_metrics_role"
  assume_role_policy = file("./policies/lambda_exec_policy.json")
}

# --------------------- DYNAMO TABLES ---------------------


resource "aws_dynamodb_table" "token_bytes" {
  name             = "TokenBytes"
  hash_key         = "filename"
  billing_mode   = "PROVISIONED"
  read_capacity  = 1
  write_capacity = 1
  attribute {
    name = "filename"
    type = "S"
  }
}

resource "aws_dynamodb_table" "semantic_tokens" {
  name             = "SemanticTokens"
  hash_key         = "filename"
  billing_mode   = "PROVISIONED"

  read_capacity  = 1
  write_capacity = 1
  attribute {
    name = "filename"
    type = "S"
  }
}

# --------------------- API GATEWAY DEFINITION ---------------------

resource "aws_api_gateway_rest_api" "api" {
  body = jsonencode({
    openapi = "3.0.1"
    info = {
      title   = "api gateway"
      version = "1.0"
    }
    paths = {
      "/namer" = {
        get = {
          x-amazon-apigateway-integration = {
            httpMethod           = "POST"
            payloadFormatVersion = "1.0"
            type                 = "AWS_PROXY"
            uri                     = aws_lambda_function.name_suggester.invoke_arn
          }
        }
      },
      "/metrics" = {
        get = {
          x-amazon-apigateway-integration = {
            httpMethod           = "POST"
            payloadFormatVersion = "1.0"
            type                 = "AWS_PROXY"
            uri                  = aws_lambda_function.event_writer.invoke_arn
          }
        }
      }
    }
  })

  name = "api gateway"
}

resource "aws_api_gateway_deployment" "api" {
  rest_api_id = aws_api_gateway_rest_api.api.id

  triggers = {
    redeployment = sha1(jsonencode(aws_api_gateway_rest_api.api.body))
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "api" {
  deployment_id = aws_api_gateway_deployment.api.id
  rest_api_id   = aws_api_gateway_rest_api.api.id
  stage_name    = "gitradar"
}

output "instance_ip_addr" {
  value = aws_api_gateway_rest_api.api.id
}


# --------------------- EVENT BRIDGE DEFINITION ---------------------

module "eventbridge" {
  source = "terraform-aws-modules/eventbridge/aws"

  create_bus = false

  rules = {
    code_file_added = {
      description   = "Trigger parser when object is added to S3"
      event_pattern = jsonencode({
            "detail" : {"EventType" : ["CodeUploaded"]}
            "source": ["s3.gitradar-codefiles"]
        })
      enabled       = true
    },
    new_event_to_write    = {
      description   = "Trigger lambda to store events"
      event_pattern = jsonencode({
           "source" : [ { "anything-but": { "prefix": "aws" } } ]
      })
      enabled       = true
    },
    new_event_to_personalize    = {
      description   = "Trigger lambda to personalize event"
      event_pattern = jsonencode({
           detail-type : [ { "exists": true  } ]
      })
      enabled       = true
    },
    code_file_tokenized = {
      description   = "Trigger model_trainer when file is tokenized"
      event_pattern = jsonencode({
            "detail" : {"EventType" : ["CodeTokenized"]}
            "source": ["lambda.tokenizer"]
        })
      enabled       = true
    }
  }

  targets = {
    code_file_added = [
#      {
#        name = "invoke_parser"
#        arn  = aws_lambda_function.parser.arn
#      },
      {
        name = "invoke_tokenizer"
        arn  = aws_lambda_function.tokenizer.arn
      }
    ],
    new_event_to_personalize   = [
      {
        name = "invoke_event_personalizer"
        arn  = aws_lambda_function.event_personalizer.arn
      }
    ],
    new_event_to_write = [
      {
        name = "invoke_event_writer"
        arn  = aws_lambda_function.event_writer.arn
      }
    ],
    code_file_tokenized = [
      {
        name = "invoke_model_trainer"
        arn =  aws_lambda_function.model_trainer.arn
      }
    ]
  }
}


