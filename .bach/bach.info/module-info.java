import com.github.sormuras.bach.ProjectInfo;

@ProjectInfo(
lookupExternals = @ProjectInfo.Externals(
    name = ProjectInfo.Externals.Name.JAVAFX,
    version = "16"),
tweaks = {
    @ProjectInfo.Tweak(tool = "jlink", option = "--launcher", value = "spacefx=spacefx/eu.hansolo.spacefx.Launcher")
}
)
module bach.info {
  requires com.github.sormuras.bach;
}
