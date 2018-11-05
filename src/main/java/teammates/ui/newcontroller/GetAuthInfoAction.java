package teammates.ui.newcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StringHelper;

/**
 * Action: gets user authentication information.
 *
 * <p>This does not log in or log out the user.
 */
public class GetAuthInfoAction extends Action {

    @Override
    public AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Login information is available to everyone
    }

    @Override
    public ActionResult execute() {
        UserInfo user = gateKeeper.getCurrentUser();
        String frontendUrl = getRequestParamValue("frontendUrl");
        if (frontendUrl == null) {
            frontendUrl = "";
        }

        Map<String, Object> output = new HashMap<>();
        if (user == null) {
            output.put("studentLoginUrl",
                    gateKeeper.getLoginUrl(frontendUrl + Const.WebPageURIs.STUDENT_HOME_PAGE));
            output.put("instructorLoginUrl",
                    gateKeeper.getLoginUrl(frontendUrl + Const.WebPageURIs.INSTRUCTOR_HOME_PAGE));
            output.put("adminLoginUrl",
                    gateKeeper.getLoginUrl(frontendUrl + Const.WebPageURIs.ADMIN_HOME_PAGE));
        } else {
            output.put("user", user);
            output.put("logoutUrl", gateKeeper.getLogoutUrl(frontendUrl + "/web"));
        }

        String csrfToken = StringHelper.encrypt(req.getSession().getId());
        String existingCsrfToken = HttpRequestHelper.getCookieValueFromRequest(req, Const.CsrfConfig.TOKEN_COOKIE_NAME);
        if (!csrfToken.equals(existingCsrfToken)) {
            Cookie csrfTokenCookie = new Cookie(Const.CsrfConfig.TOKEN_COOKIE_NAME, csrfToken);
            csrfTokenCookie.setSecure(!Config.isDevServer());
            csrfTokenCookie.setPath("/");
            resp.addCookie(csrfTokenCookie);
        }

        return new JsonResult(output);
    }

}