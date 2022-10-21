<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        Setup Multi Factor Auth
    <#elseif section = "header">
        Select Multi Factor Auth
    <#elseif section = "form">

        <h1>Hello ${username}</h1>
        <p>
            Please setup Multi-Factor-Authentication via one of two options:
        </p>
        <ul>
            <li><i>Use WebAuthn with FIDO authenticator:</i> Description Text about FIDO</li>
            <li><i>Use TOTP with Authenticator app:</i> Description text about TOTP</li>
        </ul>
        <form id="kc-select-mfa-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <input
                        class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                        type="submit"
                        name="webauthn"
                        value="Use WebAuthn"
                    />
                    <input
                        class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                        type="submit"
                        name="totp"
                        value="Use TOTP"
                    />
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
