<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        ${msg("mfaEnforcerFormTitle")}
    <#elseif section = "header">
        ${msg("mfaEnforcerFormTitle")}
    <#elseif section = "form">

        <h1>${msg("mfaEnforcerHello")} ${username}</h1>
        <p>
            ${msg("mfaEnforcerExplanation")}
        </p>
        <ul>
            <li><b>${msg("mfaEnforcerWebAuthnOptionTitle")}:</b> ${msg("mfaEnforcerWebAuthnOptionDescription")}</li>
            <li><b>${msg("mfaEnforcerTOTPOptionTitle")}:</b> ${msg("mfaEnforcerTOTPOptionDescription")}</li>
        </ul>
        <form id="kc-select-mfa-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <input
                        class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                        type="submit"
                        name="webauthn"
                        value="${msg("mfaEnforcerWebAuthnOptionButton")}"
                    />
                    <input
                        class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                        type="submit"
                        name="totp"
                        value="${msg("mfaEnforcerTOTPOptionButton")}"
                    />
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
