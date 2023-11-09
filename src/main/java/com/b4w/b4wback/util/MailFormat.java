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
                .small-image{
                    max-width: 25%%;
                    height: auto;
                }
            </style>
        </head>
        <body>
            <div style="text-align: center;">
                <img src='cid:img1'>
                <h1 style="text-align: center;">%s</h1>
                <p style="text-align: center;">%s</p>
                <a href="https://bid4wheels.com" style="text-align: center; color: darkgray; display: block; margin-top: 0px; margin-bottom: 60px;"><u>Bid4Wheels</u></a>
            </div>
        </body>
        </html>
    """.formatted(title, text);
    }


    public static String createWithDefaultHtmlWithUrl(String title,String text,String url){
        return """
       
        <html>
        <head>
            <title>Página de Ejemplo</title>
            <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto">
            <style>
                body {
                    font-family: 'Roboto', sans-serif;
                }
                .small-image{
                    max-width: 25%%;
                    height: auto;
                }
            </style>
        </head>
        <body>
            <div style="text-align: center;">
                <img src='cid:img1'>
                <h1 style="text-align: center;">%s</h1>
                <p style="text-align: center;">%s</p>
                <a href="%s" style="color: rgb(35, 191, 113); text-align: center; display: block;">Url </a>
                <a href="https://bid4wheels.com" style="text-align: center; color: darkgray; display: block; margin-top: 0px; margin-bottom: 60px;"><u>Bid4Wheels</u></a>
            </div>
        </body>
        </html>
    """.formatted(title, text,url);
    }
}
