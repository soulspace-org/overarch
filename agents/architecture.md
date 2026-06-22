# Overarch Architecture
All overarch code is located in ```org.soulspace.overarch```.

## Implementation Architecture
The implementation architecture is *Clean architecture*.

### ```org.soulspace.overarch.domain```
The domain core is stateless and all functions are referentially transparent. May depend on the utility layer.

Contains
* the domain model
* specs for the domain model
* functions operating on the domain model

### ```org.soulspace.overarch.application```
The application layer contains use case specific process orchestration. It depends on the domain core. May depend on the utility layer.

Contains
* ports as multimethods to be implemented in the adapter layer, one namespace per port.

### ```org.soulspace.overarch.adapter```
The adapter layer handles all IO. It depends on the application layer and the domain core.

Contains
* implementations of the multimethods from the application layer ports.

Implements
* multimethods from the 

### ```org.soulspace.overarch.util```
The utility layer contains utility functions. It is stateless and all functions are referentially transparent and has no dependencies to the other layers.
