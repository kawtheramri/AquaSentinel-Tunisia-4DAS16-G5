# \# AquaSentinel Tunisia – 4DAS16 – G5

# 

# Plateforme web de surveillance de la consommation d'eau, basée sur une architecture microservices.

# Projet du module AWD – Année universitaire 2026-2027.

# 

# \## Problématique

# La Tunisie est en situation de stress hydrique : sécheresses répétées, restrictions et coupures d'eau.

# Dans un immeuble, un hôtel ou un établissement, une fuite est souvent découverte tard, à la réception de la facture.

# AquaSentinel Tunisia permet de suivre la consommation par compteur, de détecter les fuites potentielles,

# d'organiser les interventions des techniciens, d'estimer les factures et de suivre les coupures d'eau.

# 

# \## Équipe

# | Membre | Microservice |

# |---|---|

# | Toute l'équipe | user-service (microservice commun) |

# | Loussaif Bhiri | building-meter-service |

# | Kawther Amri | monitoring-service |

# | Balkiss Youssfi | leak-detection-service |

# | Manar Fares | maintenance-service |

# | Yasmine Maatougui | billing-service |

# | Khairi Charrad | outage-service |

# 

# \## Acteurs

# \- \*\*Consommateur\*\* : consulte sa consommation, ses factures estimées et les coupures prévues.

# \- \*\*Gestionnaire\*\* : gère les bâtiments et compteurs, saisit les relevés, valide les fuites, crée les interventions.

# \- \*\*Technicien\*\* : consulte et clôture les interventions qui lui sont affectées.

# \- \*\*Administrateur\*\* : gère les utilisateurs, les tarifs et les coupures d'eau.

# 

# \## Microservices

# | Microservice | Responsabilité | Port | Technologie | Base de données |

# |---|---|---|---|---|

# | user-service | Gestion des utilisateurs (commun) | 8081 | Spring Boot | PostgreSQL |

# | building-meter-service | Bâtiments et compteurs | 8082 | Spring Boot | MySQL |

# | monitoring-service | Surveillance de la consommation | 8083 | Spring Boot | MongoDB |

# | leak-detection-service | Détection des fuites | 8084 | Python / FastAPI | PostgreSQL |

# | maintenance-service | Gestion des interventions | 8085 | Spring Boot | MySQL |

# | billing-service | Facturation et dépenses | 8086 | Spring Boot | PostgreSQL |

# | outage-service | Gestion des coupures d'eau | 8087 | Spring Boot | MySQL |

# 

# \## Architecture

# !\[Architecture globale](Documentation/Diagrams/architecture.png)

# 

# \- \*\*Front-end\*\* : Angular

# \- \*\*API Gateway\*\* : Spring Cloud Gateway (port 8080)

# \- \*\*Découverte des services\*\* : Eureka Server (port 8761)

# \- \*\*Configuration centralisée\*\* : Spring Cloud Config Server (port 8888)

# \- \*\*Sécurité\*\* : Keycloak (OAuth2 / OpenID Connect, port 8180)

# \- \*\*Communication synchrone\*\* : OpenFeign + Resilience4j

# \- \*\*Communication asynchrone\*\* : RabbitMQ

# \- \*\*Une base de données par microservice\*\*

# \- \*\*Conteneurisation\*\* : Docker et Docker Compose

# 

# \## Structure du dépôt

# ```

# AquaSentinel-Tunisia-4DAS16-G5/

# ├── Backend/            # un projet par microservice + Eureka, Config Server, Gateway

# ├── Frontend/           # application Angular

# ├── Documentation/

# │   ├── Presentation/   # présentation de la séance 4

# │   ├── Diagrams/       # architecture, cas d'utilisation, classes, séquences

# │   └── Dossier technique (Word)

# └── README.md

# ```

# 

# \## Lancement

# Ordre de démarrage : Eureka Server → Config Server → microservices → API Gateway.

# 

# ```bash

# cd Backend/eureka-server \&\& mvn spring-boot:run

# cd Backend/config-server \&\& mvn spring-boot:run

# cd Backend/<nom-du-service> \&\& mvn spring-boot:run

# cd Backend/api-gateway \&\& mvn spring-boot:run

# ```

# 

# Service Python :

# ```bash

# cd Backend/leak-detection-service

# pip install -r requirements.txt

# uvicorn app.main:app --port 8084

# ```

# 

# Avec Docker :

# ```bash

# cd Backend

# docker compose up --build

# ```

# 

# \## Organisation du travail

# \- Branches : `main`, `develop`, `feature/<service>-<tâche>`

# \- Fusion par pull request relue par un autre membre

# \- Commits : `feat:`, `fix:`, `docs:`, `chore:`

# \- Suivi des tâches : GitHub Projects (tableau Kanban)

