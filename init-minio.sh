#!/bin/sh

until mc alias set myminio http://minio:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD"; do
  echo "Waiting for MinIO to start..."
  sleep 2
done

mc mb "myminio/$MINIO_BUCKET_NAME" --ignore-existing

mc anonymous set download "myminio/$MINIO_BUCKET_NAME"

mc cp /images/* "myminio/$MINIO_BUCKET_NAME/"

echo "MinIO initialization completed successfully!"