package build;

import com.github.sormuras.bach.Bach;
import com.github.sormuras.bach.BuildProgram;
import com.github.sormuras.bach.Builder;
import com.github.sormuras.bach.project.ModuleLookup;
import com.github.sormuras.bach.project.Project;
import com.github.sormuras.bach.project.ProjectInfo;

public class Program implements BuildProgram {

  public Program() {}

  @Override
  public void build(Bach bach, String... args) {
    var project = Project.of(getClass().getModule().getAnnotation(ProjectInfo.class));
    new Custom(bach, project).build();
  }

  private static class Custom extends Builder {

    private Custom(Bach bach, Project project) {
      super(bach, project);
    }

    @Override
    public ModuleLookup computeModuleLookup() {
      return ModuleLookup.compose(new ModuleLookup.JavaFX("16-ea+5"), super.computeModuleLookup());
    }
  }
}
