<#import "lib/main.ftl" as u>

<@u.page title="Profile Page">

<div class="container">

    <div class="page-header text-center">
        <h1><span class="fa fa-anchor"></span> Profile Page</h1>
        <a href="/logout" class="btn btn-default btn-sm">Logout</a>

        <#if goodMessage??>
            <div class="alert alert-success">${goodMessage}</div>
        <#elseif badMessage??>
            <div class="alert alert-danger">${badMessage}</div>
        </#if>

    </div>

    <div class="row">

        <!-- LOCAL INFORMATION -->
        <div class="col-md-6">
            <div class="well">
                <h3><span class="fa fa-user"></span> Local</h3>

                <p>
                    <strong>id</strong>: ${id}<br>
                    <strong>name</strong>: ${fullname}<br>
                    <strong>email</strong>: ${email}<br>
                </p>

                <hr>
                <h3>Change Password</h3>

                <form action="/passwordReset" method="post">
                    <div class="form-group">
                        <label>Current Password</label>
                        <input type="password" class="form-control" name="oldPassword">
                    </div>
                    <div class="form-group">
                        <label>New Password</label>
                        <input type="password" class="form-control" name="newPassword">
                    </div>
                    <div class="form-group">
                        <label>Retype Password</label>
                        <input type="password" class="form-control" name="retypePassword">
                    </div>

                    <button type="submit" class="btn btn-warning btn-lg">CHANGE</button>
                </form>

            </div>
        </div>
    </div>

</div>
</@u.page>