package `in`.architectengineer.common.htmlTemplates

fun getEmailVerificationCodeTemplate(username:String, code: String): String{

    return """

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Verification</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            text-align: center;
        }

        .container {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #333333;
            font-weight: bold;
        }

        p {
            color: #555555;
            font-size: 16px;
            line-height: 1.6;
        }

        .verification-code {
            font-size: 24px;
            font-weight: bold;
            color: #007bff;
        }
        .greeting {
            font-size: 18px;
            color: #000;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>
    
    <div class="container">
        <a href="https://architectengineer.in/" target="_blank" style="text-decoration: none;">
            <img src="https://firebasestorage.googleapis.com/v0/b/architect-engineer.appspot.com/o/logo.png?alt=media&token=9ccb7866-80e7-4e4f-9421-ba0da0161f6e" alt="ArchitectEngineer.in logo" style="scale: 0.3;">
        </a>
        <h2>Email Verification</h2>
        <p class="greeting">Hello, <b>$username</b></p>
        <p>Thank you for registering with <strong><a href="https://architectengineer.in/" target="_blank" style="color: #8D4F00; text-decoration: none;">ArchitectEngineer.in!</a></strong> To complete your registration, please use the following verification code:</p>
        <p class="verification-code">$code</p>
        <p>If you didn't register on <strong><a href="https://architectengineer.in/" target="_blank" style="color: #8D4F00; text-decoration: none;">ArchitectEngineer.in!</a></strong>, you can safely ignore this email.</p>
    </div>
</body>

</html>


        
"""

}

fun getEmailVerificationLinkTemplate(username: String,link:String): String {
    return """
        
        <!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Verification</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            text-align: center;
        }

        .container {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #333333;
            font-weight: bold;
        }

        p {
            color: #555555;
            font-size: 16px;
            line-height: 1.6;
        }

        .greeting {
            font-size: 18px;
            color: #000;
            margin-bottom: 10px;
        }

        .verification-button {
            display: inline-block;
            padding: 10px;
            background-color: #007bff;
            color: #fff;
            text-decoration: none;
            border-radius: 4px;
            margin-top: 20px;
        }

        .verification-button:hover {
            background-color: #0056b3;
        }
    </style>
</head>

<body>
    
    <div class="container">
        <a href="https://architectengineer.in/" target="_blank" style="text-decoration: none;">
            <img src="https://firebasestorage.googleapis.com/v0/b/architect-engineer.appspot.com/o/logo.png?alt=media&token=9ccb7866-80e7-4e4f-9421-ba0da0161f6e" alt="ArchitectEngineer.in logo" style="scale: 0.3;">
        </a>
        <h2>Email Verification</h2>
        <p class="greeting">Hello, <b>$username</b></p>
        <p>Thank you for registering with <strong><a href="https://architectengineer.in/" target="_blank" style="color: #8D4F00; text-decoration: none;">ArchitectEngineer.in!</a></strong> To complete your registration, click on below link to verify your email.</p>
        <a class="verification-button" href="$link" target="_blank">Verify Email</a>
        <p>If you didn't register on <strong><a href="https://architectengineer.in/" target="_blank" style="color: #8D4F00; text-decoration: none;">ArchitectEngineer.in!</a></strong>, you can safely ignore this email.</p>
    </div>
</body>

</html>
        
    """
}