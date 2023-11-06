package com.b4w.b4wback.util;

public class MailFormat {
    public static String createWithDefaultHtml(String title,String text){
        return """
       
        <html>
        <head>
            <title>Página de Ejemplo</title>
            <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto">
            <style>
                body {
                    font-family: 'Roboto', sans-serif;
                }
            </style>
        </head>
        <body>
            <div style="text-align: center;">
                <img src='cid:img1'>
                <h1 style="text-align: center;">%s</h1>
                <p style="text-align: center;">%s</p>
                <a href="https://www.ejemplo.com" style="color: rgb(35, 191, 113); text-align: center; display: block;">Url </a>
                <p style="text-align: center;"><u style="color: darkgray;">Bid4Wheels</u></p>
            </div>
        </body>
        </html>
    """.formatted(title, text);
    }
}
