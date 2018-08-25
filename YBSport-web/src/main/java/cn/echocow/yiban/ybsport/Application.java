package cn.echocow.yiban.ybsport;

import cn.echocow.yiban.ybsport.convert.ConvertDbVerticle;
import cn.echocow.yiban.ybsport.convert.ConvertRestVerticle;
import cn.echocow.yiban.ybsport.utils.ConfigFactory;
import cn.echocow.yiban.ybsport.utils.VertxSingleton;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import static io.vertx.core.spi.resolver.ResolverProvider.DISABLE_DNS_RESOLVER_PROP_NAME;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 启动器
 * @date 2018-08-23 22:49
 * <p>
 * -----------------------------
 **/
public class Application extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = VertxSingleton.VERTX;
        vertx.deployVerticle(Application.class.getName());
    }

    @Override
    public void start(Future<Void> startFuture) {
        System.getProperties().setProperty(DISABLE_DNS_RESOLVER_PROP_NAME,"true");
        Future<String> dbVerticleDeployment = Future.future();
        vertx.deployVerticle(new ConvertDbVerticle(), dbVerticleDeployment.completer());
        dbVerticleDeployment.compose(id -> {
            Future<String> convertRestVerticleDeployment = Future.future();
            vertx.deployVerticle(
                    ConvertRestVerticle.class.getName(),
                    new DeploymentOptions().setInstances(1),
                    convertRestVerticleDeployment.completer());
            return convertRestVerticleDeployment;
        }).setHandler(ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }
}
