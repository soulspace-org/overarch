# Best Practices
Here are some best practices or guidelines from my practical use of Overarch.

The biggest advice is: **Be consistent in your modelling!**

Consistency is the key to features like selection based views and artifact
generation. It also helps you to navigate and maintain huge models.

# Modelling
## Model Structure And Ids
* Split big models into multiple folders and files. This helps to keep the
  model files small and comprehensible.
* The namespace of the element id should reflect the folder structure of the
  model.
* When refactoring ids, use the find & replace functionality of your editor/IDE
  to replace every occurence in all the model files at once, so the referential
  integrity of the model keeps intact.
* Use refs or `:contained-in` relations to create the hierarchical structure
  across different folders and files.
  * with `:contained-in` relations you don't have to touch the parent when
    adding new children
  * The hierarchies modelled with the `:ct` attribute is converted to
    `:contained-in` relations with the attribute `:synthetic` set to `true`
    when the model is read.


## Relations
* Use specific relation types instead of general `:rel`, if possible.
* The id of a relation should start with the id of the referrer node (the `:from`
  id) followed by a verb based on the relation type and the name part of the id
  of the referred node (the `:to` id), so
  * the naming scheme is consistent
  * you know in which file you have to look for the relation

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
