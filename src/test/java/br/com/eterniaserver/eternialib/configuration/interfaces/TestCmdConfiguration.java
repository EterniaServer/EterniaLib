package br.com.eterniaserver.eternialib.configuration.interfaces;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.commands.CommandManager;
import br.com.eterniaserver.eternialib.configuration.CommandLocale;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;

import br.com.eterniaserver.eternialib.configuration.enums.PathType;
import co.aikar.commands.CommandReplacements;

import org.bukkit.configuration.file.FileConfiguration;

import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

class TestCmdConfiguration {

    enum TestEnum {COMPLETED, UNCOMPLETED}

    @Test
    void testAddCommandCompleted() {
        CommandManager manager = Mockito.mock(CommandManager.class);
        CommandReplacements commandReplacements = Mockito.mock(CommandReplacements.class);

        FileConfiguration inFile = Mockito.mock(FileConfiguration.class);
        FileConfiguration outFile = Mockito.mock(FileConfiguration.class);

        Mockito.when(manager.getCommandReplacements()).thenReturn(commandReplacements);

        Mockito.when(inFile.getString(PathType.COMMAND_NAME.getPath(TestEnum.COMPLETED), "name")).thenReturn("name");
        Mockito.when(inFile.getString(PathType.COMMAND_SYNTAX.getPath(TestEnum.COMPLETED), "syntax")).thenReturn("syntax");
        Mockito.when(inFile.getString(PathType.COMMAND_DESCRIPTION.getPath(TestEnum.COMPLETED), "description")).thenReturn("description");
        Mockito.when(inFile.getString(PathType.COMMAND_PERMISSION.getPath(TestEnum.COMPLETED), "perm")).thenReturn("perm");
        Mockito.when(inFile.getString(PathType.COMMAND_ALIASES.getPath(TestEnum.COMPLETED), "aliases")).thenReturn("aliases");

        CmdConfiguration<TestEnum> cmdConfiguration = new CmdConfiguration<>() {
            @Override
            public FileConfiguration inFileConfiguration() {
                return inFile;
            }

            @Override
            public FileConfiguration outFileConfiguration() {
                return outFile;
            }

            @Override
            public String getFolderPath() {return "";}
            @Override
            public String getFilePath() {return "";}
            @Override
            public ConfigurationCategory category() {return null;}
            @Override
            public void executeConfig() {inFile.saveToString();}
            @Override
            public void executeCritical() {this.executeConfig();}
        };

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getCmdManager).thenReturn(manager);

            CommandLocale commandLocale = new CommandLocale(
                    "name",
                    "syntax",
                    "description",
                    "perm",
                    "aliases"
            );

            cmdConfiguration.addCommandLocale(TestEnum.COMPLETED, commandLocale);

            Mockito.verify(inFile, Mockito.times(1)).getString("commands.COMPLETED.name", "name");
            Mockito.verify(inFile, Mockito.times(1)).getString("commands.COMPLETED.syntax", "syntax");
            Mockito.verify(inFile, Mockito.times(1)).getString("commands.COMPLETED.description", "description");
            Mockito.verify(inFile, Mockito.times(1)).getString("commands.COMPLETED.permission", "perm");
            Mockito.verify(inFile, Mockito.times(1)).getString("commands.COMPLETED.aliases", "aliases");


            Mockito.verify(outFile, Mockito.times(1)).set("commands.COMPLETED.name", "name");
            Mockito.verify(outFile, Mockito.times(1)).set("commands.COMPLETED.syntax", "syntax");
            Mockito.verify(outFile, Mockito.times(1)).set("commands.COMPLETED.description", "description");
            Mockito.verify(outFile, Mockito.times(1)).set("commands.COMPLETED.permission", "perm");
            Mockito.verify(outFile, Mockito.times(1)).set("commands.COMPLETED.aliases", "aliases");
        }
    }

}
