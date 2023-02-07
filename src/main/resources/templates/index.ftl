<#import "macros/base.ftl" as base>

<@base.page "DID4DCAT">
    <div>
        <h1>User Actions</h1>

        <form action="/action/initadmin" method="post">
            <input class="button is-info" type="submit" value="Init Admin" />
        </form>

        <form action="/action/createuser" method="post">
            <input type="submit" value="Create User" />
        </form>

        <br />

        <h1>Transactions</h1>
        <form action="/action/code" method="post">
            <textarea rows="10" cols="100" name="transaction"></textarea>
            <br />
            <input type="submit" name="submit_transaction" class="btn btn-primary" value="Submit Transaction" />
            <input type="submit" name="evaluate_transaction" class="btn btn-primary" value="Evaluate Transaction" />
        </form>

        <br />
        <form action="/action/command" method="post">
            <label for="command">Command</label>
            <input type="text" name="command" id="command" />
            <label for="args">Args</label>
            <input type="text" name="args" id="args" />
            <input type="submit" value="Execute" />
        </form>


        <br />

        <#if message??>
            <div>
                ${message}
            </div>
        </#if>
    </div>
</@base.page>




