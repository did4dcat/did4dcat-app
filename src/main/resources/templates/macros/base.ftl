<#import "header.ftl" as header>
<#macro page title>
    <!DOCTYPE html>
    <html lang="en" class="h-100">
    <head>
        <title>${title}</title>
        <link href="/static/css/bootstrap.min.css" rel="stylesheet">
        <link href="/static/css/style.css" rel="stylesheet">
    </head>

    <body class="d-flex flex-column h-100">
        <@header.header />
        <div class="container p-4 mb-5" id="main-container">
            <#if error??>
                <div class="alert alert-danger" role="alert">
                    ${error}
                </div>
            </#if>
            <#if success??>
                <div class="alert alert-success" role="alert">
                    ${success}
                </div>
            </#if>
            <#nested>
        </div>
        <footer class="footer mt-auto py-3 bg-dark">
            <div class="container">
                <span class="text-muted">DID4DCAT, 2023 Fraunhofer FOKUS</span>
            </div>
        </footer>
    <script src="/static/js/bootstrap.bundle.min.js"></script>
    </body>
    </html>
</#macro>
