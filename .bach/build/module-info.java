import com.github.sormuras.bach.project.Feature;
import com.github.sormuras.bach.project.ProjectInfo;
import com.github.sormuras.bach.project.ProjectInfo.ExternalModules;
import com.github.sormuras.bach.project.ProjectInfo.ExternalModules.Link;

@ProjectInfo(
    features = Feature.GENERATE_CUSTOM_RUNTIME_IMAGE,
    launchCustomRuntimeImageWithModule = "spacefx/eu.hansolo.Launcher",
    externalModules =
        @ExternalModules(
            links = {
              @Link(
                  module = "jpro.webapi",
                  to =
                      "https://sandec.bintray.com/repo/com/sandec/jpro/jpro-webapi/2020.1.1/jpro-webapi-2020.1.1.jar"),
              @Link(
                  module = "one.jpro.sound",
                  to =
                      "https://sandec.bintray.com/repo/one/jpro/one.jpro.sound/0.1.0/one.jpro.sound-0.1.0.jar")
            }))
module build {
  requires com.github.sormuras.bach;

  provides com.github.sormuras.bach.project.ModuleLookup with build.ModulesOfJavaFX_16_ea;
}
