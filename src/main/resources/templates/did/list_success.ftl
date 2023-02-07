<#import "../macros/base.ftl" as base>

<@base.page "DID4DCAT">
    <h1>List of DIDs</h1>
    <#if didList?has_content>
        <#list didList as did>
            <pre>${did}</pre>
        </#list>
    <#else>
        <p>You don't have any DIDs yet.</p>
    </#if>
</@base.page>
