plugins {
	id("org.quiltmc.loom") version "1.0.+"
	id("org.ajoberstar.grgit") version "4.1.0"
	checkstyle
}

fun prop(key: String) = project.property(key) as String

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

base.archivesName.set(prop("archives_base_name"))
project.version = "${prop("mod_version")}-rev.${grgit.head().abbreviatedId}"
project.group = prop("maven_group")

val minecraftVersion = prop("minecraft_version")

repositories {
	maven("https://maven.shedaniel.me/")
	maven("https://www.cursemaven.com")
	maven("https://maven.terraformersmc.com/releases")
	maven("https://hephaestus.dev/release")
	maven("https://api.modrinth.com/maven")
	maven("https://maven.tterrag.com/")
	maven("https://mvn.devos.one/snapshots/")
	maven("https://maven.willbl.dev/releases/")

	maven {
		name = "Ladysnake Mods"
		url = uri("https://ladysnake.jfrog.io/artifactory/mods")
	}

	maven {
		url = uri("https://repo.sleeping.town")
		content {
			includeGroup("com.unascribed")
		}
	}

	maven {
		url = uri("https://maven.jamieswhiteshirt.com/libs-release")
		content {
			includeGroup("com.jamieswhiteshirt")
		}
	}
	maven {
		name = "AlexIIL"
		url = uri("https://maven.alexiil.uk/")
	}
	maven {
		name = "Gegy"
		url = uri("https://maven.gegy.dev")
	}

	maven("https://lazurite.dev/maven")
	maven("https://jitpack.io/")
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings("org.quiltmc:quilt-mappings:$minecraftVersion+build.${prop("mappings_version")}:intermediary-v2")

	modImplementation("org.quiltmc:quilt-loader:${prop("loader_version")}")
	modImplementation("org.quiltmc.quilted-fabric-api:quilted-fabric-api:${prop("quilted_version")}-$minecraftVersion")

	modImplementation("com.terraformersmc:modmenu:${prop("mod_menu_version")}") {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "net.fabricmc")
	}

	include(modApi("maven.modrinth:stonecutter_recipe_tags:4.2.1-fabric")!!)

	include(modApi("com.unascribed:lib39-core:1.4.2")!!)
	include(modApi("com.unascribed:lib39-recoil:1.4.2")!!)

	include(modImplementation("maven.modrinth:gravity-api:${prop("gravity_api_version")}") {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "net.fabricmc")
		exclude(group = "dev.onyxstudios.cardinal-components-api")
	})

	include(modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:5.0.2") {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "net.fabricmc")
	})

	include(modImplementation("com.github.LlamaLad7.MixinExtras:mixinextras-fabric:0.2.0-beta.5")!!)
	annotationProcessor("com.github.LlamaLad7.MixinExtras:mixinextras-fabric:0.2.0-beta.5")

	include(modImplementation("maven.modrinth:midnightlib:1.0.0-quilt")!!)

	modCompileOnly("com.simibubi.create:create-fabric-1.19.2:0.5.0.i-930+1.19.2")
	modCompileOnly("amymialee:visible-barriers:1.5.3+1.19.x")

	include(implementation("net.objecthunter:exp4j:0.4.8")!!)

	modCompileOnly(("alexiil.mc.lib:libmultipart-all:0.8.0"))

	modCompileOnly("dev.lambdaurora:lambdynamiclights:2.2.0+1.19.2")

	modCompileOnly("com.github.Virtuoel:Pehkui:3.6.3", {
		exclude(group = "net.fabricmc.fabric-api")
	})

	modCompileOnly("dev.lazurite:rayon-fabric:1.6.1")

	include(modImplementation("maven.modrinth:json-entity-animation:0.2+1.19.2")!!)
}

checkstyle {
	toolVersion = "10.7.0"
	isIgnoreFailures = false
	maxWarnings = prop("checkstyle_max_warns").toInt()
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("quilt.mod.json") {
		expand("version" to project.version)
	}
}

tasks.compileJava {
	options.encoding = "UTF-8"
	options.release.set(17)
}

java {
	withSourcesJar()
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}
