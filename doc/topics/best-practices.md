# Best Practices

Be consistent in your modelling!

# Modelling
## Model Structure And Id Namespaces
* Split big models into multiple folders and files
* 
* The namespace of the element id should reflect the folder structure of the model


## Relations
* The id of a relation should start with the id of the referrer (the `:from` reference)
* Use specific relation types instead of general `:rel`, if possible

## Views
* Use selection criteria and includes to specify the content of views
* Use refs in a view to customize elements for the specific view

# Templates
* Use the provided templates as a base for customizations
