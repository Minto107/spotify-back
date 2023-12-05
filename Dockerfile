FROM gradle:8-jdk17-focal AS build

WORKDIR /app

RUN apt update && apt upgrade -y && apt install -y ssh git zsh

RUN sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)" "" --unattended

COPY build.gradle gradlew gradlew.bat settings.gradle ./

RUN gradle dependencies --refresh-dependencies

COPY . .

RUN chsh -s $(which zsh)

CMD ["gradle", "bootRun"]