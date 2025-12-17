# NumDev - Application Full Stack

Projet OpenClassrooms visant à implémenter les tests unitaires, d'intégration et end-to-end d'une application Full-Stack.

## 📋 Table des matières

- [Prérequis](#prérequis)
- [Installation](#installation)
- [Lancement du projet](#lancement-du-projet)
- [Tests](#tests)
  - [Tests Frontend (Jest)](#tests-frontend-jest)
  - [Tests End-to-End (Cypress)](#tests-end-to-end-cypress)
  - [Tests Backend (JUnit)](#tests-backend-junit)
- [Rapports de couverture](#rapports-de-couverture)
- [Structure du projet](#structure-du-projet)

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **Node.js** (version 14.x ou supérieure)
- **npm** (version 6.x ou supérieure)
- **Java JDK** (version 11 ou supérieure)
- **Maven** (version 3.6 ou supérieure)
- **MySQL** (version 8.x ou supérieure)

## 🚀 Installation

### 1. Cloner le repository

```bash
git clone https://github.com/sunny-biard/NumDev.git
cd NumDev
```

### 2. Configuration de la base de données

Créez une base de données MySQL :

```sql
CREATE DATABASE numdev_db;
CREATE USER 'numdev_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON numdev_db.* TO 'numdev_user'@'localhost';
FLUSH PRIVILEGES;
```

Chargez le schéma et les données initiales avec le script SQL fourni :

```bash
mysql -u numdev_user -p numdev_db < ressources/sql/script.sql
```

Ou depuis MySQL :

```sql
USE numdev_db;
SOURCE /chemin/vers/NumDev/ressources/sql/script.sql;
```

### 3. Installation du Backend

```bash
cd back
```

Configurez le fichier `src/main/resources/application.properties` avec vos identifiants :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/numdev_db
spring.datasource.username=numdev_user
spring.datasource.password=password
```

Installez les dépendances Maven :

```bash
mvn clean install
```

### 4. Installation du Frontend

```bash
cd ../front
npm install
```

## 🎯 Lancement du projet

### Démarrer le Backend

Depuis le dossier `back` :

```bash
mvn spring-boot:run
```

Le backend sera accessible sur `http://localhost:8080`

### Démarrer le Frontend

Depuis le dossier `front` :

```bash
npm start
```

Le frontend sera accessible sur `http://localhost:4200`

## 🧪 Tests

### Tests Frontend (Jest)

Les tests Jest permettent de tester les composants, services et autres éléments du frontend de manière unitaire.

#### Lancer tous les tests

```bash
cd front
npm run test
```

### Tests End-to-End (Cypress)

Les tests Cypress simulent l'interaction d'un utilisateur réel avec l'application.

#### Ouvrir l'interface Cypress

```bash
cd front
npm run e2e
```

Cette commande ouvre l'interface graphique de Cypress où vous pouvez sélectionner et lancer les tests individuellement.

#### Lancer les tests Cypress en mode headless

```bash
npm run e2e:ci
```

### Tests Backend (JUnit)

Les tests JUnit testent la logique métier, les contrôleurs et les services du backend.

#### Lancer tous les tests

```bash
cd back
mvn test
```

#### Lancer un test spécifique

```bash
mvn test -Dtest=NomDuTest
```

## 📊 Rapports de couverture

### Couverture Frontend (Jest)

Le rapport est généré dans :

```
front/coverage/
```

Pour visualiser le rapport HTML, ouvrez le fichier :

```
front/coverage/lcov-report/index.html
```

### Couverture End-to-End (Cypress)

Le rapport est généré dans :

```
front/coverage/
```

Pour visualiser le rapport HTML, ouvrez le fichier :

```
front/coverage/lcov-report/index.html
```

### Couverture Backend (JaCoCo)

Le rapport est généré dans :

```
back/target/site/jacoco/
```

Pour visualiser le rapport HTML, ouvrez le fichier :

```
back/target/site/jacoco/index.html
```

## 📁 Structure du projet

```
NumDev/
├── back/                      # Application Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Code source Java
│   │   │   └── resources/    # Fichiers de configuration
│   │   └── test/
│   │       └── java/         # Tests JUnit
│   ├── pom.xml               # Configuration Maven
│   └── target/               # Fichiers compilés et rapports
│
├── front/                     # Application Frontend
│   ├── src/
│   │   ├── app/              # Composants Angular
│   │   ├── assets/           # Ressources statiques
│   │   └── environments/     # Configuration par environnement
│   ├── cypress/              # Tests E2E Cypress
│   │   ├── integration/      # Fichiers de tests
│   │   └── support/          # Fichiers de support
│   ├── coverage/             # Rapports de couverture
│   ├── package.json          # Dépendances npm
│   └── jest.config.js        # Configuration Jest
│
└── ressources/               # Ressources du projet
```

## 📝 Scripts disponibles

### Frontend

| Script | Description |
|--------|-------------|
| `npm start` | Démarre le serveur de développement |
| `npm run build` | Build de production |
| `npm run test` | Lance les tests Jest |
| `npm run e2e` | Ouvre l'interface Cypress |
| `npm run e2e:ci` | Lance Cypress en mode headless |

### Backend

| Commande | Description |
|----------|-------------|
| `mvn spring-boot:run` | Démarre l'application |
| `mvn test` | Lance les tests JUnit |
| `mvn clean test` | Nettoie et lance les tests |
| `mvn jacoco:report` | Génère le rapport de couverture |
| `mvn clean install` | Build complet du projet |
