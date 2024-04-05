# Image-app

Image-app is a Rest API, that is capable of storing and resizing images, download specific image or all of the images.

## Installation

Starting database container:

```
docker run --hostname=59f9a920943f --mac-address=02:42:ac:11:00:02 --env=POSTGRES_PASSWORD=password --env=PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin --env=LANG=en_US.utf8 --env=PG_MAJOR=16 --env=PG_VERSION=16.2 --env=PG_SHA256=446e88294dbc2c9085ab4b7061a646fa604b4bec03521d5ea671c2e5ad9b2952 --env=DOCKER_PG_LLVM_DEPS=llvm15-dev	clang15 --env=PGDATA=/var/lib/postgresql/data --volume=/var/lib/postgresql/data -p 5432:5432 --restart=no --runtime=runc -d postgres:alpine
```
I suggest high-availabe database with regular backups in production, this is only for demo purposes.

The database is created on the 5432 port, and the application assumes it this way.

For database creation you can use [pgAdmin4](https://www.pgadmin.org/download/pgadmin-4-windows/), the database name must be: image-app.

Starting backend:

Create a new project in IntelliJ IDEA with "Starting new project from version control" and copy this link: 

https://github.com/pajszerm/image-app.git

Reload all Maven projects and start the backend using the IDE built in functionality.

## Endpoints

POST api/files -> uploading and resizing images

GET api/file/<image.name> -> downloading specific image

GET api/files -> downloading all images in zip format

## Backend

- Java version 17
- SpringBoot version 3.2.4
- Database: PostgreSQL

## Specifications:

- The application is compatible with ImageMagick and GraphicsMagick to resize images. You can set wich ImageProcessor to use with the @Primary annotation.
- At the start of the application it checks whether an AFS Key file is exists and generates it if not.
- When uploading images, all images must have a width and a height to be resized to.
- Only different named images can be stored in the database.
