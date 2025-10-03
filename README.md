# Divar User Analysis - Query Engine

A command-line query engine for analyzing user profiles from Divar marketplace data. Built with Java for high performance and reliability.

## Features

- **Fast In-Memory Analysis**: Loads 800 profiles in ~3 seconds
- **Intelligent Classification**: Multi-layered classifiers with scoring algorithms
- **Dynamic Tagging**: Add custom tags to users for flexible categorization
- **User-friendly CLI**: Clean, formatted output with execution time tracking
- **Performance Monitoring**: Toggle-able query timing display

## Architecture

```
src/
├── Main.java                    # Application entry point
├── cli/                         # Command-line interface
│   ├── CommandParser.java       # Parses user commands
│   └── QueryExecutor.java       # Executes queries with formatting
├── config/
│   └── Config.java              # Global configuration
├── engine/
│   └── QueryEngine.java         # Core query orchestration
├── model/
│   └── UserProfile.java         # User data model
├── parser/
│   └── ProfileParser.java       # Markdown file parser
├── store/
│   └── DataStore.java           # Multi-index in-memory storage
└── classifier/                  # Classification algorithms
    ├── Classifier.java          # Interface
    ├── FraudClassifier.java     # Fraud detection (50+ patterns)
    ├── BusinessClassifier.java  # Business identification
    ├── RealEstateClassifier.java # Real estate agent detection
    └── NewUserClassifier.java   # New user identification
```

## Compilation

```bash
# Compile all source files
find src -name "*.java" | xargs javac -d out

# Or use individual compilation
javac -d out src/**/*.java src/**/**/*.java
```

## Usage

```bash
# Run with default data directory (data/profiles)
java -cp out Main

# Run with custom data directory
java -cp out Main /path/to/profiles
```

## Available Commands

### Query Commands
- `FIND <category>` - Find all users in a category
  - Built-in categories: `fraudsters`, `businesses`, `real_estate_agents`, `new_users`
  - Custom tags: any tag you've added via `ADD_TAG`

- `GET_USER_PROFILE <userId>` - Display full profile for a user
  - Example: `GET_USER_PROFILE user_756`

- `ADD_TAG <userId> <tag>` - Add a custom tag to a user
  - Example: `ADD_TAG user_200 vip_customer`

- `REMOVE_TAG <userId> <tag>` - Remove a tag from a user
  - Example: `REMOVE_TAG user_200 vip_customer`

### Utility Commands
- `toggle_time` - Enable/disable execution time display
- `exit` or `quit` - Exit the application

## Example Session

```
╔═══════════════════════════════════════════════════════════╗
║         Divar User Analysis - Query Engine v1.0           ║
╚═══════════════════════════════════════════════════════════╝

📂 Loading profiles from: data/profiles
✓ Loaded 800 profiles
⏱  Loaded in 3.692 seconds
─────────────────────────────────────────────────────────────

> FIND fraudsters

┌─ FIND: fraudsters
├─ Results: 165 user(s)
└─
   • user_710
   • user_756
   • user_723
   ...
⏱  Query executed in 0.002 seconds
─────────────────────────────────────────────────────────────

> ADD_TAG user_756 confirmed_scammer

┌─ ADD_TAG
├─ User: user_756
├─ Tag: confirmed_scammer
└─
   ✓ Tag 'confirmed_scammer' added to user_756
⏱  Query executed in 0.001 seconds
─────────────────────────────────────────────────────────────
```

## Classifier Details

### FraudClassifier
**Threshold**: 5+ points

- Explicit fraud keywords (5 pts): کلاهبردار, فریب
- Scam behaviors (2-4 pts): بیعانه گرفت, سر کار نیامد
- Platform abuse (2-3 pts): duplicate posts, zero publish rate
- Verified reports (3 pts): ReliableReport mentions
- Multi-city scams (3 pts): same product in 3+ cities

### BusinessClassifier  
**Threshold**: 5+ points

- Business identity (4 pts): business_type, agency mentions
- Professional services (1-3 pts): مشاور, خدمات, باربری
- High activity (2-4 pts): ≥5 posts, high revenue
- Multiple categories (2 pts)

### RealEstateClassifier
**Threshold**: 6+ points

- Explicit agent keywords (6 pts): مشاور املاک, آژانس املاک
- Real estate categories (1-4 pts): apartment-sell, commercial-rent
- Professional terms (1-3 pts): متراژ, رهن, ودیعه
- Business boost (3 pts): already classified as business

### NewUserClassifier
Multiple detection strategies:
- Very low activity (≤2 total actions)
- No supply with minimal demand (≤15 searches)
- Failed first post (0 published)
- Limited engagement (<50 views, <15 searches)

## Performance

- **Load Time**: ~3.5 seconds for 800 profiles
- **Query Time**: <0.01 seconds for most queries
- **Memory**: ~20MB for 800 user profiles

## Configuration

Toggle execution time display:
```bash
> toggle_time
⏱  Execution time display: ✗ OFF
```

Modify `Config.java` for permanent changes:
```java
Config.setShowExecutionTime(false);
```

## Data Format

Profiles are stored as Markdown files:
- Filename: `user_XXX.md`
- Header: UUID and generation timestamp
- Content: Persian text analysis with metrics

## License

Proprietary - Divar Project Challenge

## Author

Built as a solution for The Divar challenge
