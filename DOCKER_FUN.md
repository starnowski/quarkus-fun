1. Pull the Docker Image from the Source Repository
First, pull the image from the source repository. This can be a public or a private repository.

sh

docker pull source-repo/image-name:tag
For example:

sh

docker pull docker.io/myuser/myimage:latest
2. Tag the Docker Image for the Target Repository
Next, tag the image with the new target repository. This involves re-tagging the existing image with the new repository name and tag.

sh

docker tag source-repo/image-name:tag target-repo/image-name:tag
For example:

sh

docker tag myuser/myimage:latest newuser/myimage:latest
3. Log in to the Target Repository
If the target repository requires authentication, log in using the appropriate Docker command. This will prompt you for your username and password.

sh

docker login target-repo
For example, to log in to Docker Hub:

sh

docker login docker.io
Or for Amazon ECR:

sh

aws ecr get-login-password --region region | docker login --username AWS --password-stdin aws_account_id.dkr.ecr.region.amazonaws.com
4. Push the Docker Image to the Target Repository
Finally, push the re-tagged image to the target repository.

sh

docker push target-repo/image-name:tag
For example:

sh

docker push newuser/myimage:latest
Complete Example
Assuming you want to move an image from Docker Hub user olduser to Docker Hub user newuser, here are the complete commands:

Pull the image from the source repository:

sh

docker pull olduser/myimage:latest
Tag the image for the target repository:

sh

docker tag olduser/myimage:latest newuser/myimage:latest
Log in to the target repository:

sh

docker login docker.io
Push the image to the target repository:

sh

docker push newuser/myimage:latest
