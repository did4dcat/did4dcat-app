<#import "../macros/base.ftl" as base>

<@base.page "DID4DCAT">
    <h1>Login</h1>
    <form method="post">
        <div class="mb-3 mt-3 form-floating">
            <textarea class="form-control" placeholder="Copy our Identity here" id="userIdentity" name="userIdentity" style="height: 200px"></textarea>
            <label for="userIdentity">Copy our Identity here</label>
            <div id="emailHelp" class="form-text">Your Identity includes a public key</div>
        </div>
        <button type="submit" class="btn btn-primary">Login</button>
    </form>
</@base.page>



