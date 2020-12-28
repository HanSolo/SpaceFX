import com.github.sormuras.bach.project.Feature;
import com.github.sormuras.bach.project.ProjectInfo;

@ProjectInfo(
    version = "1.0",
    features = Feature.GENERATE_CUSTOM_RUNTIME_IMAGE,
    launchCustomRuntimeImageWithModule = "spacefx/eu.hansolo.spacefx.Launcher"
)
module build {
  requires com.github.sormuras.bach;

  provides com.github.sormuras.bach.BuildProgram with build.Program;
}
