# Utiliser une image de base Python
FROM openjdk:22-jdk-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /samake

# Copier les fichiers de votre application dans le conteneur
COPY  /target/Samake-0.0.1-SNAPSHOT.jar /samake/Samake-0.0.1-SNAPSHOT.jar

# Exposer le port sur lequel l'application Spring Boot fonctionnera
EXPOSE 8080

# Commande à exécuter pour lancer l'application
ENTRYPOINT ["java", "-jar", "/samake/Samake-0.0.1-SNAPSHOT.jar"]
