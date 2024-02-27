@AuthHook
@PostsHooks
Feature: Posts pages

  Scenario: Posts

    # Check for post availability on the posts page
    Then Open url "http://localhost:8084/posts"
    Then Content on tag "h2" with text "Posts" visible
    Then Content on tag "a" containing text "post to delete" visible
    Then Click "post to delete" link
    Then Content on tag "h2" with text "Post" visible
    Then Content on tag "span" with text "post to delete" visible
    Then Content on tag "h3" with text "Comments" visible