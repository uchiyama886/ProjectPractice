# 使用する Docker image
FROM eclipse-temurin:23-jdk

# Ubuntu package 類のインストール
RUN apt -y update && apt -y upgrade
RUN apt -y install \
    # For development and debug
    bash curl vim net-tools zip \
    # Build tools
    build-essential make \
    # Apache Ant for Java builds
    ant \
    && apt clean \
    && rm -rf /var/lib/apt/lists/*

# Docker container ログイン時の作業ディレクトリを指定
ARG workdir="working_dir"
WORKDIR /${workdir}
