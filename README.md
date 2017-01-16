# jira-test
JIRA test client  

To run application using Maven:  
1. Add atlassian public maven repository (https://maven.atlassian.com/repository/public) to your maven settings.xml  
2. To build project run: $ mvn package  
3. To build distribution packages run: $ mvn assembly:assembly  
4. Change directory to target  
5. To run project execute: $ java -jar jira-test-0.0.1-SNAPSHOT.jar   

You can add your Jira server URL and credentials to conf.xml to use default.  

Application has intuitive console interface.  
Example of creating issue:  

Please select action from listed below:  
1 - Get all issues
2 - Update specific issue description
3 - Create new issue
4 - Close specific issue
5 - Quit application

Please enter number from listed above!

3
Wait please...
Please select project from listed below:
1 - Test Task (TT)

Please enter number from listed above!

1
Please select issue type from listed below:
1 - Task
2 - Bug
3 - Sub-task
4 - Epic
5 - Story

Please enter number from listed above!

1
Wait please...
Please select priority from listed below:
1 - Highest
2 - High
3 - Medium
4 - Low
5 - Lowest

Please enter number from listed above!

2
Please enter value for Summary
test summary
Please enter value for Description
test description
Wait please...
New issue has been created successfully!
New issue key is TT-41
-----------

Please select action from listed below:
1 - Get all issues
2 - Update specific issue description
3 - Create new issue
4 - Close specific issue
5 - Quit application



