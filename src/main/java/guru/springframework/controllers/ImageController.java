package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
public class ImageController {
    final private ImageService imageService;
    final private RecipeService recipeService;

    @Autowired
    public ImageController(ImageService imageService, RecipeService recipeService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @GetMapping("recipe/{id}/image")
    public String getImageForm(@PathVariable String id, Model model) {
        try {
            model.addAttribute("recipe", recipeService.findCommandById(Long.parseLong(id)));
        } catch (NumberFormatException e) {
            log.error("Invalid id", e);
        }
        return "/recipe/imageuploadform";
    }

    @GetMapping(value = "recipe/{id}/recipeimage", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage(@PathVariable String id) {
        try {
            RecipeCommand recipeCommand = recipeService.findCommandById(Long.parseLong(id));
            if (recipeCommand != null && recipeCommand.getImage() != null) {
                byte[] bytes = new byte[recipeCommand.getImage().length];
                int i = 0;
                for (Byte b : recipeCommand.getImage()) {
                    bytes[i++] = b;
                }
                return bytes;
            }
        } catch (NumberFormatException e) {
            log.error("Invalid id", e);
        }
        return null;
    }

    @PostMapping("recipe/{id}/image")
    public String saveOrUpdate(@PathVariable String id, @RequestParam("imagefile") MultipartFile file) {
        imageService.saveImageFile(Long.parseLong(id), file);
        log.debug("saved recipe id: " + id);
        return "redirect:/recipe/" + id + "/show";
    }
}
