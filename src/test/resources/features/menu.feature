Feature: Menu items

  Scenario: Menu items

    Then Open url "http://localhost:8084"

#    users
    Then Click on menu item "Users"
    Then Content on tag "h2" with text "Users" visible

#    posts
    Then Click on menu item "Posts"
    Then Content on tag "h2" with text "Posts" visible

#    log in
    Then Click on menu auth-item "login"
    Then Content on tag "h2" with text "Log in" visible

#    registration
    Then Open url "http://localhost:8084"
    Then Click on menu auth-item "register"
    Then Content on tag "h2" with text "Registration" visible