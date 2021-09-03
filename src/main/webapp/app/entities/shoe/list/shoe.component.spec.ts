jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ShoeService } from '../service/shoe.service';

import { ShoeComponent } from './shoe.component';

describe('Component Tests', () => {
  describe('Shoe Management Component', () => {
    let comp: ShoeComponent;
    let fixture: ComponentFixture<ShoeComponent>;
    let service: ShoeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ShoeComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(ShoeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ShoeComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ShoeService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.shoes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
