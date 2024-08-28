# Best Practices
Here are some best practices or guidelines from my practical use of Overarch.

The biggest advice is: *Be consistent in your modelling!*

# Modelling
## Model Structure And Ids
* Split big models into multiple folders and files. This helps to keep the
  model files small and comprehensible.
* The namespace of the element id should reflect the folder structure of the
  model.
* When refactoring ids, use the find & replace functionality of your editor/IDE
  to replace every occurence in the model files at once, so the referential
  integrity of the model keeps intact.
* Use refs or `:contained-in` relations to create the hierarchical structure
  across different folders and files.


## Relations
* The id of a relation should start with the id of the referrer (the `:from`
  reference), then you know in which file you have to look for the relation.
* Use specific relation types instead of general `:rel`, if possible.

## Views
* Use selection criteria and includes to specify the content of views.
* Use refs in a view to customize elements for the specific view, e.g to
  override directions for relations.
* By creating a standard set of views with consistent naming, it's easier to
  generate a consistent documentation via templates.

# Templates
* Use the provided templates as a base for customizations.
* Use just the namespace of the element for path generation, elaborate path
  configurations (with namespace-prefix/-suffix) makes link generation
  difficult.
