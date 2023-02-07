<#import "../macros/base.ftl" as base>

<@base.page "DID4DCAT">
    <h1>Update DID</h1>
    <form method="post">
        <div class="mb-3 mt-3 form-floating">
            <input type="did" class="form-control" id="did" name="did" />
            <label for="did">ID</label>
        </div>
        <div class="mb-3 mt-3 form-floating">
            <input type="url" class="form-control" id="url" name="url" />
            <label for="url">URL</label>
        </div>
        <div class="mb-3 mt-3 form-floating">
            <input type="text" class="form-control" id="hash" name="hash" />
            <label for="url">Hash (Optional)</label>
        </div>
        <button type="submit" class="btn btn-primary">Update</button>
    </form>
</@base.page>



