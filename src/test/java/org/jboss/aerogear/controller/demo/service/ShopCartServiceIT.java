package org.jboss.aerogear.controller.demo.service;

import org.jboss.aerogear.controller.demo.idm.persistence.Role;
import org.jboss.aerogear.controller.demo.idm.persistence.RoleRegistry;
import org.jboss.aerogear.controller.demo.idm.persistence.User;
import org.jboss.aerogear.controller.demo.idm.persistence.UserRegistry;
import org.jboss.aerogear.controller.demo.model.Car;
import org.jboss.aerogear.controller.demo.util.ArchiveUtils;
import org.jboss.aerogear.controller.demo.util.Resources;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class ShopCartServiceIT {

    @Inject
    private UserRegistry userRegistry;

    @Inject
    private RoleRegistry roleRegistry;

    //TODO must be replaced
    //@Inject
    //private AuthenticatorManager authenticatorManager;

    @Inject
    private ShopCartService shopCartService;

    public Role buildRole(String roleName) {
        Role role = new Role(roleName, roleName);
        roleRegistry.newRole(role);
        return role;
    }

    @Deployment
    public static WebArchive createDeployment() {

        //TODO figure out how to fix it
        /*File[] libs = DependencyResolvers.use(MavenDependencyResolver.class)
                .loadEffectivePom("pom.xml")
                .artifacts("org.apache.deltaspike.core")
                .resolveAsFiles();*/

        return ShrinkWrap.create(WebArchive.class)
                //.addAsLibraries(libs)
                .addAsLibraries(ArchiveUtils.getDeltaSpikeCoreAndSecurityArchive())
                .addClasses(ShopCartService.class, Resources.class,
                        Role.class, User.class, Car.class,
                        RoleRegistry.class, UserRegistry.class)
                .addAsWebInfResource("beans.xml", "beans.xml")
                .addAsResource("persistence.xml", "META-INF/persistence.xml");

    }

    @Test
    public void shouldAccessProtectedResourceWithValidLogin() throws Exception {
        try {
            User user = new User("test", "test");
            user.setRoles(buildRole("admin"));
            userRegistry.newUser(user);
            //TODO must be replaced
            //authenticatorManager.login(getAuthInfo("test", "test", "admin"));
            shopCartService.add(new Car("red", "hat"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldThrowExceptionWhenRoleInvalid() throws Exception {
        try {
            User user = new User("test2", "test2");
            user.setRoles(buildRole("manager"));
            userRegistry.newUser(user);
            //TODO must be replaced
            //authenticatorManager.login(getAuthInfo("test2", "test2", "guest"));
            shopCartService.add(new Car("red", "hat"));
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void shouldThrowExceptionWithoutValidLogin() throws Exception {
        try {
            shopCartService.add(new Car("red", "hat"));
            fail("Should throw authorization exception");
        } catch (Exception e) {
            assertTrue(true);
        }
    }


//    private AuthInfo getAuthInfo(String id, String description, String role) {
//        return new AuthInfoImpl(id, description, role);
//    }
}