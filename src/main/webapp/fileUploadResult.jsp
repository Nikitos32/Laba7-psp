<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>File Upload Result</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 20px;
    }

    h1 {
      color: navy;
    }

    ul {
      list-style-type: none;
      padding: 0;
    }

    li {
      margin-bottom: 20px;
    }

    strong {
      font-weight: bold;
    }

    pre {
      background-color: #f5f5f5;
      padding: 10px;
      border-radius: 5px;
      white-space: pre-wrap;
    }
  </style>
</head>
<body>
<h1>File Upload Result</h1>
<ul>
  <% for (int i = 0; i < ((List<String>) request.getAttribute("uploadedFiles")).size(); i++) { %>
  <li>
    <strong>File Name:</strong> <%= ((List<String>) request.getAttribute("uploadedFiles")).get(i) %><br>
    <strong>File Content:</strong><br>
    <pre><%= ((List<String>) request.getAttribute("fileContents")).get(i) %></pre>
  </li>
  <% } %>
</ul>
</body>
</html>