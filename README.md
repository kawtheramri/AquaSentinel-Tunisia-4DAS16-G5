<div align="center">

# 💧 AquaSentinel Tunisia

### Surveiller l'eau, détecter les fuites, agir vite

![Classe](https://img.shields.io/badge/Classe-4DAS16-0B3C5D?style=for-the-badge)
![Groupe](https://img.shields.io/badge/Groupe-G5-1C9AA8?style=for-the-badge)
![Module](https://img.shields.io/badge/Module-AWD-7C3AED?style=for-the-badge)
![Année](https://img.shields.io/badge/2026--2027-ESPRIT-E11D48?style=for-the-badge)

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-DD0031?style=flat-square&logo=angular&logoColor=white)
![GraphQL](https://img.shields.io/badge/GraphQL-E10098?style=flat-square&logo=graphql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat-square&logo=rabbitmq&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-4D4D4D?style=flat-square&logo=keycloak&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=flat-square&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)

*Plateforme web de suivi de la consommation d'eau – architecture microservices*
*Encadré par **Mr Ghassen Hamouda***

</div>

---

## 🚨 Problématique
La Tunisie est en **stress hydrique** : sécheresses répétées, restrictions et coupures d'eau.
Dans un immeuble, un hôtel ou un établissement, une fuite est souvent découverte **tard, au moment de la facture**.

> 🔄 **Cycle complet :** Relevé ➜ Fuite détectée ➜ Intervention (technicien + pièce) ➜ Facture et économies
>
> 🧪 Les relevés viennent d'un **simulateur** pour la démonstration.

## 👥 Acteurs
| | Acteur | Rôle |
|:--:|---|---|
| 🏠 | **Consommateur** | Consulte la consommation et les factures de son bâtiment |
| 🧑‍💼 | **Gestionnaire** | Gère ses bâtiments, suit les relevés, vérifie les fuites, suit les interventions et les factures |
| 🔧 | **Technicien** | Reçoit ses interventions, utilise les pièces réservées, rédige le compte rendu |
| 🛡️ | **Administrateur** | Gère les comptes et les rôles, le stock de pièces et les tarifs |
| 🤖 | **Simulateur** | Envoie automatiquement des relevés de consommation |

## 🏗️ Architecture globale
<div align="center">
  <img src="Documentation/Diagrams/architecture.png" alt="Architecture globale" width="900"/>
</div>

- 🌐 **Front-end Angular** ➜ **API Gateway** (Spring Cloud Gateway), point d'entrée unique
- 🧭 **Eureka** (découverte) · ⚙️ **Config Server** (configuration) · 🔐 **Keycloak** (JWT, rôles)
- 🔗 Synchrone : **OpenFeign + Resilience4j** · 📨 Asynchrone : **RabbitMQ**
- 🗄️ **Une base de données par microservice**, aucune clé étrangère entre les bases
- 🐳 Conteneurisation : **Docker + Docker Compose**

## 🧩 Microservices
| Microservice | Responsabilité | Base | Responsable |
|---|---|:--:|---|
| 🟡 **Building Service** (COMMUN) | Référentiel des bâtiments, compteurs, gestionnaires – REST + **GraphQL** | ![MySQL](https://img.shields.io/badge/-MySQL-4479A1?style=flat-square) | Toute l'équipe |
| 🔵 **User Service** | Comptes, rôles, authentification, disponibilité des techniciens | ![MySQL](https://img.shields.io/badge/-MySQL-4479A1?style=flat-square) | Manar Fares |
| 🔵 **Water Consumption Service** | Enregistrement, historique, agrégation et correction des relevés | ![PostgreSQL](https://img.shields.io/badge/-PostgreSQL-4169E1?style=flat-square) | Yasmine Maatougui |
| 🔵 **Leak Detection Service** | Détection des fuites, niveau de risque, perte estimée | ![MongoDB](https://img.shields.io/badge/-MongoDB-47A248?style=flat-square) | Kawther Amri |
| 🔵 **Maintenance Service** | Interventions, de la fuite détectée jusqu'à la clôture | ![MySQL](https://img.shields.io/badge/-MySQL-4479A1?style=flat-square) | Loussaif Bhiri |
| 🔵 **Spare Parts Service** | Stock, réservation et sortie des pièces | ![MySQL](https://img.shields.io/badge/-MySQL-4479A1?style=flat-square) | Balkiss Youssfi |
| 🔵 **Billing Service** | Factures par tranches, majorations et économies | ![MySQL](https://img.shields.io/badge/-MySQL-4479A1?style=flat-square) | Khairi Charrad |

> ⭐ **6 microservices individuels + 1 microservice commun** (Building), développé par toute l'équipe avec la technologie avancée **GraphQL**.

## 📨 Principaux événements (RabbitMQ)
| Événement | Producteur | Consommateurs |
|---|---|---|
| `MeasurementRecorded` | Water Consumption | Leak Detection |
| `LeakDetected` | Leak Detection | Maintenance |
| `MaintenanceAssigned` | Maintenance | User |
| `MaintenanceCompleted / Cancelled` | Maintenance | User, Leak Detection, Spare Parts, Billing |
| `PartOutOfStock` | Spare Parts | Maintenance |
| `BuildingUpdated` | Building | Water Consumption, Leak Detection, Billing |
| `UserDeactivated` | User | Building, Maintenance |

## 📁 Structure du dépôt
```
AquaSentinel-Tunisia-4DAS16-G5/
├── Backend/
│   ├── serveur-eureka/  serveur-config/  passerelle-api/
│   ├── service-batiments/          (COMMUN)
│   ├── service-utilisateurs/
│   ├── service-consommation/
│   ├── service-detection-fuites/
│   ├── service-interventions/
│   ├── service-pieces-rechange/
│   └── service-facturation/
├── Frontend/
├── Documentation/
│   ├── Presentation/
│   ├── Diagrams/
│   └── Dossier_Technique_AquaSentinel.docx
└── README.md
```

## 🚀 Lancement (prévu)
Ordre : `serveur-eureka` ➜ `serveur-config` ➜ microservices ➜ `passerelle-api`
```bash
cd Backend/<nom-du-service>
mvn spring-boot:run
```
Avec Docker :
```bash
cd Backend
docker compose up --build
```

## 🗂️ Organisation du travail
- 🌿 Une branche par tâche : `feature/<service>-<tâche>`
- 🔍 Pull request relue par un autre membre avant la fusion
- 👤 Chaque membre commite sur son propre microservice
- 📋 Suivi des tâches : **GitHub Projects (Kanban)**

## 👩‍💻 Équipe
<div align="center">

| Loussaif Amine Bhiri | Kawther Amri | Balkiss Youssfi | Manar Fares | Yasmine Maatougui | Khairi Charrad |
|:--:|:--:|:--:|:--:|:--:|:--:|
| Maintenance | Leak Detection | Spare Parts | User | Water Consumption | Billing |

</div>