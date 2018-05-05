FROM ubuntu:16.04

RUN apt-get update -y && \
	apt-get install -y \
	openjdk-8-jdk \
	openjdk-8-jre \
	maven \
	tree \
	nano \
	mc \
	sudo \
	mongodb \
	wget && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/*

RUN useradd --create-home --shell /bin/bash ubuntu && \
	echo "ubuntu:ubuntu" | chpasswd && \
	adduser ubuntu sudo && \
	echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

ENV HOME /home/ubuntu
ENV SPARK_VERSION 2.3.0
ENV HADOOP_VERSION 2.7
ENV MONGO_SPARK_VERSION 2.2.0
ENV SCALA_VERSION 2.10
ENV HADOOP_SOLO_VERSION 2.9.0
ENV SPARK_HOME ${HOME}/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}
ENV PATH ${PATH}:${SPARK_HOME}/bin
ENV HADOOP_HOME ${HOME}/hadoop-${HADOOP_SOLO_VERSION}
ENV HADOOP_COMMON_LIB_NATIVE_DIR ${HADOOP_HOME}/lib/native
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/

WORKDIR ${HOME}

RUN chown -R ubuntu:ubuntu /home/ubuntu
USER ubuntu

# get spark
RUN wget http://apache.mirror.digitalpacific.com.au/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz && \
tar xvf spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz

# get hadoop
RUN wget https://archive.apache.org/dist/hadoop/core/hadoop-${HADOOP_SOLO_VERSION}/hadoop-${HADOOP_SOLO_VERSION}.tar.gz && \
tar xvf hadoop-${HADOOP_SOLO_VERSION}.tar.gz

RUN rm -fv hadoop-${HADOOP_SOLO_VERSION}/hadoop-${HADOOP_SOLO_VERSION}.tar.gz && \
	rm -fv spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz

COPY . /home/ubuntu/

#RUN sudo chown -R ubuntu:ubuntu /home/ubuntu

RUN sudo mvn package

EXPOSE 27017