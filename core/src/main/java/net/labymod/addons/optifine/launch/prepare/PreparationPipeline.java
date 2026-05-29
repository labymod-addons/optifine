/*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package net.labymod.addons.optifine.launch.prepare;

import java.nio.file.Path;
import java.util.List;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.api.util.logging.Logging;

/**
 * Runs the OptiFine install pipeline by feeding each {@link PreparationStage}'s output into the
 * next. Any unexpected stage failure is wrapped into an {@link OptiFineException} naming the stage.
 */
public class PreparationPipeline {

  private static final Logging LOGGER = Logging.getLogger();

  private final List<PreparationStage> stages;

  public PreparationPipeline(List<PreparationStage> stages) {
    this.stages = stages;
  }

  public Path run(PreparationContext context) throws OptiFineException {
    Path current = context.rawOptiFineJar();
    for (PreparationStage stage : this.stages) {
      LOGGER.debug("Running OptiFine preparation stage '{}'", stage.name());
      try {
        current = stage.run(context, current);
      } catch (OptiFineException exception) {
        throw exception;
      } catch (Exception exception) {
        throw new OptiFineException(
            "OptiFine preparation stage '" + stage.name() + "' failed",
            exception
        );
      }
    }
    return current;
  }
}
