@AuthHook
@PostsHooks
Feature: Comments pages

  Scenario: Comments

    # Adding comment
    Then Open url "http://localhost:8084/posts"
    Then Content on tag "h2" with text "Posts" visible
    Then Content on tag "a" containing text "post to delete" visible
    Then Click "post to delete" link
    Then Content on tag "h2" with text "Post" visible
    Then Enter "comment to delete" in the "textarea" field by name "content" of form with id "comment-adding-form"
    Then Click "Add comment" input-submit of form with id "comment-adding-form"
    Then Content on tag "h2" with text "Post" visible
    Then Content on tag "span" with text "comment to delete" visible

    # Check for comment availability on the user page
    Then Open url "http://localhost:8084/users/self"
    Then Content on tag "h2" with text "User" visible
    Then Content on tag "a" containing text "comment to delete" visible

    # Deleting comment
    Then Click containing text "comment to delete" link
    Then Content on tag "h2" with text "Post" visible
    Then Click "Delete" input-submit of form with id "comment-delete-form"
    Then Content on tag "h2" with text "User" visible