import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrnSelectImports } from '@spartan-ng/brain/select';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmCarousel, HlmCarouselContent, HlmCarouselItem } from "@spartan-ng/helm/carousel";
import { HlmFormFieldImports } from '@spartan-ng/helm/form-field';
import { HlmInput } from '@spartan-ng/helm/input';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CardData } from '../../interface/CardData';


@Component({
  selector: 'app-league',
  imports: [
    HlmFormFieldImports,
    HlmSelectImports,
    HlmInput,
    HlmSelectImports,
    BrnSelectImports,
    HlmButtonImports,
    ReactiveFormsModule
  ],
  templateUrl: './league.html',
  styleUrl: './league.css',
})

export class League {

// Determines number of items you'll see wtihin carousel

public cardList: CardData[] = [
    { id: 1, title: 'Football League', description: 'NFL 2025 Season', buttonText: 'View' },
    { id: 2, title: 'Basketball League', description: 'NBA Playoffs 2025', buttonText: 'View' },
    { id: 3, title: 'Hockey League', description: 'NHL Stanley Cup 2025', buttonText: 'View' },
  ];

}
