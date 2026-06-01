# Architecture

## System Diagram

```mermaid
flowchart LR
  subgraph wizard-registry["Wizard Registry Service"]
    wizard-import-resource["Wizard Import Resource"]
    wizard-report-resource["Wizard Report Resource"]
    wizard-resource["Wizard Resource"]
  end

  wizard-registry-application-i-t-service["Wizard Registry Application I T Service"]

  wizard-import-resource -->|HTTPS| wizard-registry-application-i-t-service
```

## Components

### Services

- **WizardImportResource**: POST /api/wizards/import
- **WizardReportResource**: GET /api/reports/{filename}
- **WizardResource**: POST /api/wizards, GET /api/wizards/{id}, GET /api/wizards, +3 more

### External Services

- **Wizard Registry Application I T Service**: HTTPS service

## Reference

For the complete CALM (Common Architecture Language Model) schema, see [calm-architecture.json](calm-architecture.json).
