@AuthHook
Feature: Users pages

  Scenario: Users page
    Then Open url "http://localhost:8084/users"
    Then Content on tag "h2" with text "Users" visible
    Then Click "delete-username" link
    Then Content on tag "h2" with text "User" visible
    Then Content on tag "span" with text "delete-username" visible

  Scenario: User page
    Then Open url "http://localhost:8084/users/self"
    Then Content on tag "h2" with text "User" visible
    Then Content on tag "span" with text "delete-username" visible
    Then Content on tag "span" with text "2012-11-01" visible
    Then Content on tag "span" with text "+1234598765" visible
    Then Content on tag "h3" with text "Posts" visible
    Then Content on tag "h3" with text "Comments made by user" visible

  Scenario: Editing user page
    Then Open url "http://localhost:8084/users/edit"
    Then Content on tag "h2" with text "Editing user" visible
    Then Enter "+5678954321" in the input field by name "phoneNumber"
    Then Enter "31.01.2021" in the input field by name "birthDate"
    Then Click "Confirm updating" input-submit
    Then Content on tag "h2" with text "User" visible
    Then Content on tag "span" with text "delete-username" visible
    Then Content on tag "span" with text "2021-01-31" visible
    Then Content on tag "span" with text "+5678954321" visible
