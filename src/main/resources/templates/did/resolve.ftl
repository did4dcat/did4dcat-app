<#import "../macros/base.ftl" as base>

<@base.page "DID4DCAT">
    <h1>Resolve DID</h1>
    <form method="post">
        <div class="mb-3 mt-3 form-floating">
            <input type="did" class="form-control" id="did" name="did" />
            <label for="did">DID</label>
        </div>
        <button type="submit" class="btn btn-primary">Resolve</button>
    </form>
</@base.page>



