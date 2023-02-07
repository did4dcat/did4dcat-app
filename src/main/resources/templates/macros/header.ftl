<#import "utils.ftl" as utils>

<#macro header>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="/"><strong>DID4DCAT</strong></a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                    aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <#if page??>
                            <#if page == "did/resolve">
                                <a class="nav-link active" href="/did/resolve">Resolve DID</a>
                            <#else>
                                <a class="nav-link" href="/did/resolve">Resolve DID</a>
                            </#if>
                        <#else>
                            <a class="nav-link" href="/did/resolve">Resolve DID</a>
                        </#if>
                    </li>
                    <@utils.auth>
                    <li class="nav-item">
                        <#if page??>
                            <#if page == "did/list">
                                <a class="nav-link active" href="/did/list">List DIDs</a>
                            <#else>
                                <a class="nav-link" href="/did/list">List DIDs</a>
                            </#if>
                        <#else>
                            <a class="nav-link" href="/did/list">List DIDs</a>
                        </#if>
                    </li>
                    </@utils.auth>
                    <@utils.auth>
                        <li class="nav-item">
                            <#if page??>
                                <#if page == "did/create">
                                    <a class="nav-link active" href="/did/create">Create DID</a>
                                <#else>
                                    <a class="nav-link" href="/did/create">Create DID</a>
                                </#if>
                            <#else>
                                <a class="nav-link" href="/did/create">Create DID</a>
                            </#if>
                        </li>
                    </@utils.auth>
                    <@utils.auth>
                    <li class="nav-item">
                        <#if page??>
                            <#if page == "did/update">
                                <a class="nav-link active" href="/did/update">Update DID</a>
                            <#else>
                                <a class="nav-link" href="/did/update">Update DID</a>
                            </#if>
                        <#else>
                            <a class="nav-link" href="/did/update">Update DID</a>
                        </#if>
                    </li>
                    <li class="nav-item">
                        <#if page??>
                            <#if page == "user/did">
                                <a class="nav-link active" href="/user/did">My DID</a>
                            <#else>
                                <a class="nav-link" href="/user/did">My DID</a>
                            </#if>
                        <#else>
                            <a class="nav-link" href="/user/did">My DID</a>
                        </#if>
                    </li>
                    </@utils.auth>
                    <!-- <@utils.auth>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
                           data-bs-toggle="dropdown" aria-expanded="false">
                            Admin
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <li><a class="dropdown-item" href="/console">Console</a></li>
                            <li><a class="dropdown-item" href="#">Create User</a></li>
                            <li><a class="dropdown-item" href="#">Manage Users</a></li>
                        </ul>
                    </li>
                    </@utils.auth> -->
                    <!-- <@utils.auth>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
                           data-bs-toggle="dropdown" aria-expanded="false">
                            User
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <li><a class="dropdown-item" href="#">Get ID</a></li>
                            <li><a class="dropdown-item" href="#">DIDs</a></li>
                        </ul>
                    </li>
                    </@utils.auth> -->
                </ul>
                <ul class="navbar-nav navbar-nav navbar-right">
                    <li class="nav-item">
                        <#if user??>
                            <a class="nav-link" href="/user/logout">Logout (${user.name})</a>
                        <#else>
                            <a class="nav-link" href="/user/login">Login</a>
                        </#if>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</#macro>



